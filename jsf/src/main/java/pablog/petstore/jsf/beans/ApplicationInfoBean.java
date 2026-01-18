package pablog.petstore.jsf.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

@Named("appInfo")
@ApplicationScoped
public class ApplicationInfoBean {

    private final String version;

    public ApplicationInfoBean() {
        this.version = resolveVersion();
    }

    public String getVersion() {
        return version;
    }

    private String resolveVersion() {
        final String defaultVersion = "N/A";
        final String moduleTitle = "jsf";

        try {
            Enumeration<URL> manifests = Thread.currentThread()
                .getContextClassLoader()
                .getResources("META-INF/MANIFEST.MF");

            while (manifests.hasMoreElements()) {
                URL url = manifests.nextElement();
                try (InputStream manifestStream = url.openStream()) {
                    Properties manifest = new Properties();
                    manifest.load(manifestStream);

                    String title = manifest.getProperty("Implementation-Title");
                    if (title != null && title.equalsIgnoreCase(moduleTitle)) {
                        return manifest.getProperty("Implementation-Version", defaultVersion);
                    }
                }
            }
        } catch (IOException ignored) {
            // Fall through to default
        }

        return defaultVersion;
    }
}
