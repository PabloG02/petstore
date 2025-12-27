package pablog.petstore.tests.dbunit;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * CDI-managed helper class for DBUnit operations in Arquillian integration tests.
 * This bean runs inside the container and has access to the datasource via @Resource injection.
 * 
 * <p>Provides functionality equivalent to Arquillian Persistence Extension:</p>
 * <ul>
 *   <li>Load datasets before tests (equivalent to @UsingDataSet)</li>
 *   <li>Assert database state after tests (equivalent to @ShouldMatchDataSet)</li>
 *   <li>Execute cleanup scripts (equivalent to @CleanupUsingScript)</li>
 * </ul>
 * 
 * <p>Usage in tests:</p>
 * <pre>
 * {@literal @}Inject
 * private DBUnitHelper dbUnit;
 * 
 * {@literal @}BeforeEach
 * void setUp() throws Exception {
 *     dbUnit.loadDataSet("owners.xml");
 * }
 * 
 * {@literal @}AfterEach
 * void tearDown() throws Exception {
 *     dbUnit.executeCleanupScripts("scripts/cleanup.sql");
 * }
 * </pre>
 */
@ApplicationScoped
public class DBUnitHelper {

    private static final Logger LOGGER = Logger.getLogger(DBUnitHelper.class.getName());
    
    /** Default directory for dataset files on classpath */
    private static final String DATASETS_DIR = "datasets/";
    
    /** Default schema name for H2 in WildFly */
    private static final String DEFAULT_SCHEMA = "PUBLIC";

    @Resource(lookup = "java:jboss/datasources/ExampleDS")
    private DataSource dataSource;

    private String schema = DEFAULT_SCHEMA;

    /**
     * Sets the database schema to use.
     * @param schema the schema name
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * Loads dataset files using CLEAN_INSERT operation.
     * This clears the tables involved and inserts the dataset data.
     *
     * @param datasetFiles Dataset file names (relative to datasets/ directory)
     * @throws DatabaseUnitException if dataset loading fails
     */
    public void loadDataSet(String... datasetFiles) throws DatabaseUnitException {
        if (datasetFiles == null || datasetFiles.length == 0) {
            LOGGER.fine("No dataset files specified, skipping load");
            return;
        }

        LOGGER.info(() -> "Loading datasets: " + String.join(", ", datasetFiles));

        IDatabaseConnection connection = null;
        try {
            connection = getConnection();
            IDataSet dataSet = buildDataSet(datasetFiles);
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
            LOGGER.fine("Dataset loaded successfully");
        } catch (SQLException e) {
            throw new DatabaseUnitException("Failed to load dataset", e);
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * Asserts that the current database state matches the expected dataset(s).
     *
     * @param expectedFiles Expected dataset file names
     * @throws DatabaseUnitException if assertion fails or database access fails
     * @throws AssertionError if database state doesn't match expected
     */
    public void assertDataSet(String... expectedFiles) throws DatabaseUnitException {
        assertDataSet(expectedFiles, null, null);
    }

    /**
     * Asserts that the current database state matches the expected dataset.
     *
     * @param expectedFiles Expected dataset file names
     * @param excludeColumns Columns to exclude from comparison
     * @param orderByColumns Columns to order by for comparison
     * @throws DatabaseUnitException if assertion fails or database access fails
     * @throws AssertionError if database state doesn't match expected
     */
    public void assertDataSet(String[] expectedFiles, String[] excludeColumns, String[] orderByColumns) 
            throws DatabaseUnitException {
        if (expectedFiles == null || expectedFiles.length == 0) {
            LOGGER.fine("No expected dataset files specified, skipping assertion");
            return;
        }

        LOGGER.info(() -> "Asserting dataset: " + String.join(", ", expectedFiles));

        IDatabaseConnection connection = null;
        try {
            connection = getConnection();
            IDataSet expectedDataSet = buildDataSet(expectedFiles);
            IDataSet actualDataSet = connection.createDataSet();

            for (String tableName : expectedDataSet.getTableNames()) {
                ITable expectedTable = expectedDataSet.getTable(tableName);
                ITable actualTable = actualDataSet.getTable(tableName);

                // Determine sort columns - use all columns from expected table if not specified
                Column[] sortColumns = determineSortColumns(expectedTable, orderByColumns);

                // Apply sorting BEFORE filtering to ensure consistent ordering
                expectedTable = new SortedTable(expectedTable, sortColumns);
                actualTable = new SortedTable(actualTable, sortColumns);

                // Apply column exclusions AFTER sorting
                if (excludeColumns != null && excludeColumns.length > 0) {
                    expectedTable = DefaultColumnFilter.excludedColumnsTable(expectedTable, excludeColumns);
                    actualTable = DefaultColumnFilter.excludedColumnsTable(actualTable, excludeColumns);
                }

                // Compare tables
                org.dbunit.Assertion.assertEquals(expectedTable, actualTable);
            }

            LOGGER.fine("Dataset assertion passed");
        } catch (SQLException e) {
            throw new DatabaseUnitException("Failed to assert dataset", e);
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * Determines which columns to use for sorting.
     * If orderByColumns is specified, use those. Otherwise, use all columns
     * from the expected table to ensure deterministic ordering.
     */
    private Column[] determineSortColumns(ITable expectedTable, String[] orderByColumns) 
            throws DataSetException {
        if (orderByColumns != null && orderByColumns.length > 0) {
            // Use explicitly specified columns
            return org.dbunit.dataset.Columns.findColumnsByName(
                orderByColumns, 
                expectedTable.getTableMetaData()
            );
        } else {
            // Use all columns from expected table for deterministic sorting
            return expectedTable.getTableMetaData().getColumns();
        }
    }

    /**
     * Executes SQL cleanup scripts.
     *
     * @param scriptFiles SQL script file names (relative to classpath root)
     * @throws DatabaseUnitException if script execution fails
     */
    public void executeCleanupScripts(String... scriptFiles) throws DatabaseUnitException {
        if (scriptFiles == null || scriptFiles.length == 0) {
            LOGGER.fine("No cleanup scripts specified, skipping cleanup");
            return;
        }

        LOGGER.info(() -> "Executing cleanup scripts: " + String.join(", ", scriptFiles));

        IDatabaseConnection connection = null;
        try {
            connection = getConnection();
            Connection jdbcConnection = connection.getConnection();
            
            for (String scriptFile : scriptFiles) {
                executeScript(jdbcConnection, scriptFile);
            }
            
            LOGGER.fine("Cleanup scripts executed successfully");
        } catch (SQLException | IOException e) {
            throw new DatabaseUnitException("Failed to execute cleanup script", e);
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * Cleans all data from tables in the given dataset.
     * Uses DELETE_ALL operation.
     *
     * @param datasetFiles Dataset files specifying tables to clean
     * @throws DatabaseUnitException if cleanup fails
     */
    public void cleanTables(String... datasetFiles) throws DatabaseUnitException {
        if (datasetFiles == null || datasetFiles.length == 0) {
            LOGGER.fine("No dataset files specified for cleanup, skipping");
            return;
        }

        LOGGER.info(() -> "Cleaning tables from datasets: " + String.join(", ", datasetFiles));

        IDatabaseConnection connection = null;
        try {
            connection = getConnection();
            IDataSet dataSet = buildDataSet(datasetFiles);
            DatabaseOperation.DELETE_ALL.execute(connection, dataSet);
            LOGGER.fine("Tables cleaned successfully");
        } catch (SQLException e) {
            throw new DatabaseUnitException("Failed to clean tables", e);
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * Gets a DBUnit database connection from the injected datasource.
     *
     * @return Database connection
     * @throws DatabaseUnitException if connection cannot be obtained
     */
    public IDatabaseConnection getConnection() throws DatabaseUnitException {
        try {
            if (dataSource == null) {
                throw new DatabaseUnitException("DataSource not injected - ensure this bean runs inside the container");
            }
            Connection jdbcConnection = dataSource.getConnection();
            
            DatabaseConnection dbConnection = new DatabaseConnection(jdbcConnection, schema);

            // Configure DBUnit for H2 compatibility
            DatabaseConfig config = dbConnection.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new EnumFriendlyH2DataTypeFactory());

            return dbConnection;
        } catch (SQLException e) {
            throw new DatabaseUnitException("Failed to get database connection", e);
        }
    }

    /**
     * Closes the database connection safely.
     */
    private void closeConnection(IDatabaseConnection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to close database connection", e);
            }
        }
    }

    /**
     * Builds a composite dataset from multiple files.
     */
    private IDataSet buildDataSet(String... datasetFiles) throws DatabaseUnitException {
        List<IDataSet> datasets = new ArrayList<>();
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);

        for (String file : datasetFiles) {
            String resourcePath = DATASETS_DIR + file;
            URL url = getClass().getClassLoader().getResource(resourcePath);
            
            if (url == null) {
                throw new DatabaseUnitException("Dataset file not found: " + resourcePath);
            }

            try {
                datasets.add(builder.build(url));
            } catch (Exception e) {
                throw new DatabaseUnitException("Failed to parse dataset file: " + file, e);
            }
        }

        try {
            return new CompositeDataSet(datasets.toArray(new IDataSet[0]));
        } catch (Exception e) {
            throw new DatabaseUnitException("Failed to build composite dataset", e);
        }
    }

    /**
     * Executes a SQL script file.
     */
    private void executeScript(Connection connection, String scriptFile) throws IOException, SQLException {
        URL url = getClass().getClassLoader().getResource(scriptFile);
        if (url == null) {
            throw new IOException("Script file not found: " + scriptFile);
        }

        try (InputStream is = url.openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            
            String script = reader.lines().collect(Collectors.joining("\n"));
            String[] statements = script.split(";");

            try (Statement stmt = connection.createStatement()) {
                for (String sql : statements) {
                    sql = sql.trim();
                    if (!sql.isEmpty() && !sql.startsWith("--")) {
                        try {
                            stmt.execute(sql);
                        } catch (SQLException e) {
                            LOGGER.log(Level.WARNING, "Failed to execute SQL: " + sql, e);
                            // Continue with next statement for cleanup scripts
                        }
                    }
                }
            }
        }
    }

    /** Maps H2 ENUM columns (reported as OTHER) to VARCHAR for DBUnit. */
    private static class EnumFriendlyH2DataTypeFactory extends H2DataTypeFactory {
        @Override
        public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
            if (sqlType == Types.OTHER && sqlTypeName != null && sqlTypeName.toUpperCase().startsWith("ENUM")) {
                return DataType.VARCHAR;
            }
            return super.createDataType(sqlType, sqlTypeName);
        }
    }
}
