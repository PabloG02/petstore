# Guía de contribución

## Tabla de contenidos

1. [Primeros pasos](#primeros-pasos)
2. [Arquitectura del proyecto](#arquitectura-del-proyecto)
3. [Entorno de desarrollo](#entorno-de-desarrollo)
4. [Configuración de tu entorno local](#configuración-de-tu-entorno-local)
5. [Flujo de trabajo de control de versiones](#flujo-de-trabajo-de-control-de-versiones)
6. [Estrategia de pruebas](#estrategia-de-pruebas)
7. [Estilo y estándares de código](#estilo-y-estándares-de-código)
8. [Integración continua](#integración-continua)

## 1. Primeros pasos

El proyecto **Pet Store** sigue un modelo de desarrollo Java empresarial con una clara separación de responsabilidades entre varios módulos. Antes de comenzar a contribuir, se recomienda familiarizarse con los conceptos de **Jakarta EE**, incluidos **Enterprise JavaBeans (EJB)**, **Java Persistence API (JPA)**, **Jakarta RESTful Web Services (JAX-RS)** y **Jakarta Server Faces (JSF)**.

Para poder contribuir, es necesario clonar el repositorio usando el comando `git clone [repository-url]` y configurar el entorno de desarrollo. El proyecto utiliza **Gradle** como herramienta de construcción, lo que permite gestionar dependencias y mantener un proceso de compilación consistente en distintas máquinas.

El desarrollo se realiza en un entorno de **integración continua** con **despliegue continuo** en un servidor de preproducción (staging).

## 2. Arquitectura del proyecto

La aplicación Pet Store está organizada en 7 módulos distintos, cada uno con responsabilidades específicas dentro de la arquitectura general:

- **domain**: define el modelo de dominio de la aplicación. Contiene las entidades JPA que representan los conceptos principales del negocio y su mapeo a la base de datos.
- **service**: implementa la lógica de negocio mediante EJBs sin estado. Orquesta operaciones entre entidades, gestiona transacciones y aplica reglas de seguridad basadas en roles.
- **rest**: expone la funcionalidad de la aplicación a través de servicios RESTful usando Jakarta REST. Consume la capa de servicios y se encarga de aspectos propios de HTTP como códigos de estado, manejo de errores y CORS.
- **jsf**: proporciona la interfaz web basada en Jakarta Server Faces. Incluye beans gestionados que conectan las vistas JSF con la capa de servicios, gestionando validación y feedback al usuario.
- **security**: encapsula los mecanismos de seguridad de la aplicación, incluyendo la autenticación de usuarios y el control de acceso, compartidos por la capa REST y la interfaz web.
- **tests**: agrupa utilidades y recursos compartidos para pruebas, como datos de prueba y clases auxiliares para tests de integración.
- **ear**: módulo de empaquetado que genera el Enterprise Archive (EAR) final para el despliegue en un servidor Jakarta EE.

## 3. Entorno de desarrollo

Para trabajar en el proyecto Pet Store es necesario disponer con las siguientes herramientas y servicios, que cubren el desarrollo local, la integración continua y el despliegue en distintos entornos:

- **JDK 21 o superior**: Versión de Java requerida para compilar y ejecutar el proyecto. Debe estar correctamente instalada y configurada.
- **Gradle**: Sistema de construcción utilizado en el proyecto. El repositorio incluye el Gradle Wrapper, por lo que no es necesario instalar Gradle manualmente en el equipo.
- **Git**: Sistema de control de versiones empleado para el desarrollo colaborativo. Es imprescindible para obtener el código, crear ramas y enviar cambios al repositorio.
- **[GitLab](http://gitlab.home.arpa/)**: Plataforma en la que se aloja el repositorio Git del proyecto. Se utiliza para la gestión del código fuente, revisiones mediante *merge requests* y control del historial de cambios.
- **GitLab CI**: Herramienta de integración continua encargada de ejecutar automáticamente las compilaciones, pruebas y validaciones del proyecto en cada cambio relevante del repositorio.
- **[SonarQube](http://sonar.home.arpa/)**: Servicio de análisis de calidad de código. Permite evaluar aspectos como mantenibilidad, posibles errores y problemas de seguridad, integrándose con los procesos de CI.
- **WildFly local**: Servidor de aplicaciones utilizado durante el desarrollo para ejecutar y depurar la aplicación en el entorno del desarrollador.
- **WildFly de pre-producción**: Entorno de despliegue intermedio donde se publica la aplicación tras una construcción correcta en integración continua, con el objetivo de validar su funcionamiento antes de su uso final.
- **Nexus Repository Manager**: Repositorio de artefactos donde se publican las versiones empaquetadas del proyecto destinadas a su consumo por clientes.
- **GitLab Registry**: Registro de artefactos generados durante los procesos de CI, orientado principalmente a su uso interno por parte de los desarrolladores y los pipelines.
- **MySQL 9 o superior**: Sistema gestor de bases de datos utilizado por la aplicación. Debe estar disponible en el entorno local y correctamente configurado para el proyecto.
- **Jira**: Sistema de gestión de tareas y seguimiento del trabajo. Se emplea para organizar el desarrollo, registrar incidencias y planificar el avance del proyecto.

## 4. Configuración del entorno local de desarrollo

Para preparar el entorno local de desarrollo, sigue estos pasos:

1. **Instalación del JDK**  
   Instala el [JDK 21+](https://adoptium.net/temurin/releases?version=21&os=any&arch=any). Verifica la instalación correcta ejecutando `java -version` y `javac -version`, que deberían mostrar la versión 21 o superior.

2. **Instalación de Git**  
   Necesitarás Git como sistema de control de versiones.
    * En sistemas Ubuntu o basados en Debian: `sudo apt-get install git`.
    * En Windows: `winget install --id Git.Git -i --source winget`.

3. **Clonado del repositorio**  
   Clona el repositorio del proyecto Pet Store en tu equipo local ejecutando `git clone [repository-url]` desde el directorio que prefieras. Este paso crea una copia completa del proyecto y su historial.

4. **Compilación inicial del proyecto**  
   Una vez clonado el repositorio, verifica que el proyecto compila correctamente.
    * En Linux o macOS: `./gradlew build`.
    * En Windows: `gradlew.bat build`.

   Durante la primera ejecución se descargarán las dependencias necesarias. Una compilación exitosa confirma que el entorno Java está correctamente configurado.

5. **Importación en el entorno de desarrollo**  
   El IDE recomendado es [IntelliJ IDEA](https://www.jetbrains.com/idea/download/?section=windows). Importa el proyecto como un proyecto Gradle:
   * Abre IntelliJ IDEA y selecciona **File → Open**.
   * Navega hasta el directorio del proyecto y selecciónalo.
   * IntelliJ detectará automáticamente que es un proyecto Gradle y lo importará.
   * Tras la importación, comprueba que todos los módulos se reconocen correctamente y que no hay errores de configuración.

Con esto ya es suficiente para empezar a trabajar en el proyecto. Si, además, se desea ejecutarlo de forma local, será necesario seguir las siguientes instrucciones.

### 4.1. MySQL

Al ejecutar la aplicación en local se utilizará una base de datos MySQL para almacenar los datos de forma persistente, tal y como ocurriría en una ejecución real.

Este proyecto está configurado para funcionar con MySQL 9+. A continuación se explicará cómo instalar MySQL utilizando el gestor de paquetes del sistema y utilizando Docker, para así poder aplicar el método más adecuado en cada situación.

#### 4.1.1. Instalación con gestor de paquetes

**Ubuntu/Debian:**
```bash
sudo apt install mysql-server
```

Al ejecutar este comando se mostrará información sobre la versión concreta de MySQL que se instalará. Se pueden consultar otras alternativas con:

```bash
sudo apt-cache search mysql-server
```

**macOS:**
```bash
brew install mysql
```

**Windows:**  
Descarga el instalador desde el [sitio web oficial de MySQL](https://dev.mysql.com/downloads/mysql/).

Una vez instalado MySQL, es necesario importar la base de datos. En el directorio `additional-material/db` del proyecto están almacenados los scripts de creación de la base de datos. El script `petstore-mysql.full.sql` contiene la creación completa de la base de datos, incluyendo la creación del esquema y del usuario usado por la aplicación.

En sistemas Ubuntu, se puede realizar la importación desde la raíz del proyecto con el siguiente comando:
```bash
mysql -u root -p < additional-material/db/petstore-mysql.full.sql
```

Dependiendo de la versión de Ubuntu, en lugar de este comando, puede ser necesario ejecutar:
```bash
sudo mysql < additional-material/db/petstore-mysql.full.sql
```

#### 4.1.2. Instalación con Docker

En caso de que no se disponga de una versión 9+ de MySQL para instalar, que ya se tenga una versión instalada o que se prefiera no instalarla directamente en el equipo, se puede utilizar la instalación con Docker.

En primer lugar, es necesario instalar Docker siguiendo los pasos del [manual oficial](https://docs.docker.com/engine/install/).

Una vez instalado, se puede ejecutar un contenedor con MySQL 9 utilizando el siguiente comando desde el directorio raíz del proyecto:
```bash
docker run -d --name petstore-mysql \
  -e MYSQL_ROOT_PASSWORD=petstorepass \
  -v $PWD/mysql:/var/lib/mysql \
  -v $PWD/additional-material/db/petstore-mysql.full.sql:/docker-entrypoint-initdb.d/petstore-mysql.full.sql \
  -p 3306:3306 \
  mysql:9
```

Este comando creará un contenedor llamado `petstore-mysql` con MySQL 9 y, además, se encargará de ejecutar el script `additional-material/db/petstore-mysql.full.sql` durante el primer arranque, con lo que no será necesario importarlo manualmente.

Este contenedor almacenará la base de datos en el directorio `mysql` del proyecto para que sea persistente y no se pierda si se elimina el contenedor. El proyecto está configurado para que este directorio no se envíe al sistema de control de versiones.

El ciclo de vida del contenedor se puede controlar con los siguientes comandos:

* **Iniciar el contenedor** (necesario al reiniciar el equipo):
  ```bash
  docker start petstore-mysql
  ```

* **Detener el contenedor**:
  ```bash
  docker stop petstore-mysql
  ```

* **Acceder a MySQL como root** (password: `petstorepass`):
  ```bash
  docker exec -it petstore-mysql mysql -uroot -p
  ```

* **Acceder a MySQL como `petstore`** (usuario utilizado por la aplicación; password `petstore`):
  ```bash
  docker exec -it petstore-mysql mysql -upetstore -p
  ```

### 4.2. WildFly

El proyecto está configurado y preparado para ser ejecutado en un servidor WildFly con soporte para Jakarta EE 10. Se recomienda utilizar [WildFly 38.0.1.Final](https://www.wildfly.org/downloads/).

Al desplegarse, la aplicación estará disponible en:

* **Web:** `http://localhost:8080/pet-store/jsf/`
* **REST:** `http://localhost:8080/pet-store/rest/`

#### 4.2.1. Ejecución en un WildFly local

La ejecución del proyecto en un WildFly local requiere la instalación del propio servidor, de un SGBD MySQL y la configuración del servidor WildFly.

**Instalación de WildFly:**  
Descarga una versión compatible desde [http://wildfly.org/downloads/](http://wildfly.org/downloads/) y descomprímela en un directorio local.

**Configuración de seguridad con Elytron:**

El proyecto utiliza Jakarta Security (anteriormente Java EE Security) que en WildFly se implementa mediante el subsistema Elytron. Es necesario configurar este subsistema para que la aplicación funcione correctamente.

Para configurar la seguridad, sigue estos pasos:

1. **Arrancar el servidor WildFly:**  
   Abre un terminal, navega al directorio de WildFly y arranca el servidor con el perfil por defecto:

    * **Linux/macOS:**
      ```bash
      bin/standalone.sh
      ```

    * **Windows:**
      ```cmd
      bin\standalone.bat
      ```

2. **Configurar Elytron mediante CLI:**  
   La configuración del subsistema de seguridad Elytron se realiza mediante un script CLI disponible en el repositorio oficial de WildFly Quickstarts.

   Descarga el fichero `configure-elytron.cli` desde el siguiente [enlace](https://github.com/wildfly/quickstart/blob/main/ee-security/configure-elytron.cli) y guárdalo en una ubicación accesible desde la línea de comandos.

   Abre una nueva terminal, navega al directorio donde se haya descargado el script y ejecuta el siguiente comando, reemplazando `WILDFLY_HOME` con la ruta a tu instalación de WildFly:

    * **Linux/macOS:**
      ```bash
      WILDFLY_HOME/bin/jboss-cli.sh --connect --file=additional-material/wildfly/configure-elytron.cli
      ```

    * **Windows:**
      ```cmd
      WILDFLY_HOME\bin\jboss-cli.bat --connect --file=additional-material\wildfly\configure-elytron.cli
      ```

   Al finalizar la ejecución del script, la salida debería ser similar a la siguiente:
   ```
   The batch executed successfully
   process-state: reload-required
   ```

3. **Recargar la configuración:**  
   Una vez ejecutado el script, es necesario recargar la configuración del servidor para que los cambios entren en vigor:

    * **Linux/macOS:**
      ```bash
      WILDFLY_HOME/bin/jboss-cli.sh --connect --command=reload
      ```

    * **Windows:**
      ```cmd
      WILDFLY_HOME\bin\jboss-cli.bat --connect --command=reload
      ```

Con esto ya estaría configurado el WildFly local.

A partir de este punto, en ejecuciones posteriores, bastará con arrancar el servidor invocando el siguiente comando desde el directorio de instalación de WildFly:

* **Linux/macOS:**
  ```bash
  bin/standalone.sh
  ```

* **Windows:**
  ```cmd
  bin\standalone.bat
  ```

Una vez arrancado el servidor, se puede desplegar el fichero WAR o EAR generado al realizar la construcción del proyecto. Existen dos formas de hacer esto:

**Opción A:** Copiar el fichero directamente al directorio `standalone/deployments` del servidor WildFly. El servidor debería reconocer inmediatamente la aplicación e iniciar el despliegue.

**Opción B:** Acceder a la interfaz de gestión del servidor, que debería estar disponible en `http://localhost:9990`, e ir al panel "Deployments". En este panel se tendrá la posibilidad de desplegar el fichero. Para poder realizar esta opción, es necesario añadir un usuario administrador al servidor, para lo cual se debe invocar el siguiente comando desde el directorio de WildFly:

* **Linux/macOS:**
  ```bash
  bin/add-user.sh
  ```

* **Windows:**
  ```cmd
  bin\add-user.bat
  ```

#### 4.2.2. Ejecución en un WildFly con IntelliJ

IntelliJ IDEA permite ejecutar y depurar la aplicación directamente en un servidor WildFly local. Esta opción facilita el desarrollo al permitir despliegues rápidos y depuración integrada.

**Requisitos previos:**
* Haber instalado y configurado WildFly como se describió en el [apartado 4.2.1](#421-ejecución-en-un-wildfly-local).
* Haber importado el proyecto en IntelliJ IDEA.

**Configuración de Run/Debug:**

1. Ve a **Run → Edit Configurations**.
2. Haz clic en el botón **+** (Add New Configuration) y selecciona **JBoss/WildFly Server → Local**.
3. En la pestaña **Server**:
   * **Application server:** haz clic en **Configure** y selecciona el directorio donde instalaste WildFly.
   * **JRE:** selecciona el JDK 21+ que instalaste.
4. En la pestaña **Deployment**:
   * Haz clic en **+** y selecciona **Artifact**.
   * Selecciona el artefacto EAR del proyecto (`Gradle : pablog : petstore-1.0.0-SNAPSHOT.ear (exploded)`).
5. Aplica los cambios y ejecuta la configuración.

Con esta configuración, IntelliJ se encargará de arrancar WildFly, desplegar la aplicación y conectar el depurador automáticamente.

#### 4.2.3. Definición de la propiedad jboss.home

La propiedad `jboss.home` de Gradle debe apuntar al directorio de instalación de WildFly. Esta propiedad es necesaria para la ejecución de los tests de integración.

Se puede definir de dos formas:

**Opción A:** En el fichero `gradle.properties` del proyecto:
```properties
jboss.home=/ruta/a/wildfly
```

**Opción B:** Por línea de comandos al ejecutar Gradle:
```bash
./gradlew test -Pjboss.home=/ruta/a/wildfly
```

## Flujo de trabajo con control de versiones (Git)

El proyecto Pet Store sigue un modelo de ramificación Git diseñado para dar soporte a múltiples versiones en paralelo, manteniendo la estabilidad del código mientras se permite el desarrollo continuo.

El proyecto mantiene las siguientes ramas permanentes:

- **Rama `main`**: Contiene el código de la versión activa en desarrollo. Esta es la rama principal donde se integran todas las nuevas funcionalidades. Los commits aquí deben ser estables: el código debe compilar correctamente, pasar todas las pruebas y poder desplegarse sin errores.

- **Ramas `release/vX`**: Cada versión mayor publicada tiene su propia rama de release (por ejemplo, `release/v5`, `release/v6`). Estas ramas contienen el código de las versiones en mantenimiento y solo reciben correcciones de errores críticos. Dentro de cada rama se crean tags para las versiones menores y patches (por ejemplo, `1.0.0`, `1.0.1`, `1.0.2` en la rama `release/v1`). Una vez que una versión se vuelve obsoleta (más de cuatro años), su rama puede archivarse pero no se elimina del repositorio.

Al trabajar en una tarea específica, se crean ramas temporales:

- **Ramas `feature/<name>`**: Para desarrollar nuevas funcionalidades en la versión activa. Estas ramas siempre parten de `main`. El nombre debe ser descriptivo del trabajo a realizar, por ejemplo: `feature/add-pet-vaccination-records`.

- **Ramas `fix/<name>`**: Para corregir errores. Dependiendo del alcance de la corrección:
    - Si el error solo afecta a la versión activa, la rama parte de `main`
    - Si el error afecta a versiones en mantenimiento, la rama parte de la correspondiente `release/vX` y puede necesitarse aplicar el fix a múltiples ramas

**Ningún commit debe permanecer en una rama temporal por más de una semana.** Si transcurre este periodo sin completar el trabajo, se debe proceder a hacer merge a la rama correspondiente de aquellos aspectos que estén finalizados y funcionando correctamente. Esto evita que las ramas divergan excesivamente y facilita la integración continua, incluso cuando las tareas complejas requieren más tiempo del previsto.

En estas ramas de trabajo se pueden realizar commits libremente conforme se avanza, incluso si representan trabajo en progreso. **Es importante realizar rebase diariamente** de la rama origen (`main` o `release/vX`) para mantener la rama de trabajo actualizada y minimizar los conflictos al integrar.

Una vez completado el trabajo y verificado que todas las pruebas pasen localmente, se debe hacer un último rebase de la rama objetivo para incorporar los últimos cambios. Tras resolver posibles conflictos y ejecutar nuevamente la suite completa de pruebas, se crea un pull request para fusionar la rama de vuelta a su origen, permitiendo así la revisión de código antes de la integración.

**Los merge requests de correcciones de errores deben etiquetarse apropiadamente** cuando el fix necesite aplicarse también a versiones en mantenimiento. Se utilizan labels del tipo `cherrypick:vX` (por ejemplo, `cherrypick:v3`) para facilitar la gestión y seguimiento de qué correcciones deben propagarse a qué versiones.

Cuando se publica una nueva versión mayor, se crea una nueva rama `release/vX` desde `main`, se etiqueta el commit inicial con la versión correspondiente (por ejemplo, `X.0.0`), y `main` continúa con el desarrollo de la siguiente versión mayor.

### Integración continua y manejo de construcciones fallidas

Todas las ramas del proyecto están integradas con el servidor de integración continua, que ejecuta automáticamente la suite completa de pruebas tras cada push. **Todos los commits deben ser estables**, independientemente de la rama en la que se realicen: el código debe compilar correctamente, pasar todas las pruebas y poder desplegarse sin errores.

**Si una construcción falla en cualquier rama**, es necesario revertir inmediatamente el repositorio a un estado estable. La forma más directa de hacerlo es forzar que la rama remota retroceda al commit anterior al problemático:
```bash
# Para la rama main
git push origin +HEAD^:main

# Para una rama de release
git push origin +HEAD^:release/v5

# Para una rama de trabajo
git push origin +HEAD^:feature/nombre-rama
```

Este comando sitúa la rama remota en el commit anterior a `HEAD`, que es el commit que causó el fallo. El commit problemático seguirá existiendo localmente, permitiendo corregir los errores y posteriormente realizar un `git commit --amend` para enmendarlo antes de volver a hacer push.

Si se prefiere descartar el commit local pero conservar los cambios en los archivos de trabajo, puede utilizarse:
```bash
git reset --mixed HEAD^
```

### Sincronización con el repositorio remoto

Cuando el repositorio remoto contiene commits nuevos que no están presentes localmente, Git impedirá hacer push directamente. En estos casos, es necesario traer los cambios remotos antes de poder subir los propios.

Para evitar commits de merge innecesarios, se debe utilizar rebase al hacer pull:
```bash
git pull --rebase
```

Este comando inicia un proceso de rebase que reaplica los commits locales sobre el último commit remoto, manteniendo un historial lineal y limpio.

**Configuración recomendada**: Para que este comportamiento sea el predeterminado en el proyecto, se puede configurar Git para que siempre use rebase al hacer pull:
```bash
git config pull.rebase true
```

Con esta configuración, ya no será necesario especificar `--rebase` en cada pull.

### Verificación antes de hacer pull

Antes de traer cambios del repositorio remoto, conviene verificar el estado del servidor de integración continua. **Si hay una construcción en ejecución, se debe esperar** a que finalice y confirmar que ha sido exitosa antes de hacer pull.

En caso de que una construcción falle, es necesario esperar a que el repositorio vuelva a un estado estable (mediante el proceso descrito en la [sección 6.1](#integración-continua-y-manejo-de-construcciones-fallidas)) antes de sincronizar los cambios locales.

### Trabajo en progreso y sincronización

Cuando se está en medio de un desarrollo (con cambios sin commitear) y es necesario traer commits del repositorio remoto, puede utilizarse el área de stash para guardar temporalmente el trabajo en curso:
```bash
git stash
git pull --rebase
git stash pop
```

El primer comando guarda los cambios actuales en un área temporal, el segundo trae los cambios remotos, y el tercero restaura el trabajo en progreso sobre los cambios recién descargados.

### Resolución de conflictos durante rebase

Durante un rebase pueden surgir conflictos que requieren resolución manual. Git pausará el proceso y marcará los archivos en conflicto. Tras editar estos archivos y resolver las diferencias:
```bash
git add 
git rebase --continue
```

Si en algún momento se desea abortar el rebase y volver al estado anterior:
```bash
git rebase --abort
```

### Ciclo de vida de las ramas temporales

Las ramas `feature/` y `fix/` permanecen activas en el repositorio incluso después de que su contenido haya sido fusionado mediante merge request. Esto permite continuar trabajando en la misma rama si surgen revisiones adicionales o trabajo relacionado.

**Las ramas temporales deben ser eliminadas automáticamente tras un mes de inactividad** (sin recibir nuevos commits). Este proceso de limpieza ayuda a mantener el repositorio organizado y evita la acumulación de ramas obsoletas.

Si se desea eliminar manualmente una rama que ya no se necesita, puede hacerse con:
```bash
# Eliminar rama local
git branch -d feature/nombre-rama

# Eliminar rama remota
git push origin --delete feature/nombre-rama
```

# 7. Tests

Antes de comenzar con los tests, conviene conocer el módulo `tests`. Su propósito es centralizar utilidades y herramientas que varios módulos necesitarán durante las pruebas. Cualquier clase o recurso que vaya a reutilizarse en diferentes partes del proyecto debería residir aquí.

Cada módulo tiene requisitos de testing distintos según su responsabilidad arquitectónica, por lo que la estrategia variará dependiendo de dónde estemos trabajando.

Como regla fundamental, los métodos de prueba deben redactarse de forma **simple y directa**, priorizando la claridad sobre cualquier otra consideración. Por este motivo, no se incluirá documentación Javadoc en los métodos de test (aunque las utilidades del módulo `tests` sí deben documentarse apropiadamente).

## 7.1 Estrategia por módulo

La aproximación al testing cambia según el módulo:

- **En el módulo `domain`**: se escriben tests unitarios centrados en las entidades. El foco está en verificar constructores, validaciones de propiedades y manejo de relaciones. Merece especial atención probar escenarios límite: valores nulos, cadenas que excedan límites, condiciones de frontera. Los constructores sin parámetros requeridos por JPA quedan fuera del alcance de testing.

- **En el módulo `service`**: la estrategia cambia a tests de integración que verifican el comportamiento de los Enterprise JavaBeans dentro de su contenedor natural. Mediante Arquillian se despliega una versión reducida de la aplicación en un WildFly embebido. Aquí no solo importa la lógica de negocio, sino también las restricciones de seguridad.

- **En el módulo `rest`**: se realizan tests de integración con Arquillian utilizando las extensiones Persistence y REST Client. Estos tests despliegan los recursos REST en un contenedor embebido y ejecutan peticiones HTTP reales, verificando el ciclo completo de petición-respuesta. Se valida que los recursos procesan correctamente las peticiones, transforman adecuadamente entre entidades del dominio y DTOs, devuelven los códigos de estado HTTP apropiados, y manejan correctamente la serialización, deserialización y mapeo de excepciones.

## 7.2 Herramientas del módulo tests

Este módulo concentra cuatro categorías de utilidades:

- **Test Doubles**: implementaciones alternativas de clases que simplifican el testing. `TestPrincipal`, por ejemplo, reemplaza el `Principal` de la aplicación permitiendo cambiar dinámicamente el usuario ejecutor.

- **Clases Dataset**: representan conjuntos de datos de prueba mediante código Java. Proporcionan métodos para obtener instancias de entidades útiles durante el testing. Deben organizarse en los mismos paquetes que las entidades correspondientes y mantener consistencia con los datasets XML de DBUnit.

- **Datasets XML de DBUnit**: ficheros XML que describen estados iniciales de base de datos para las pruebas. El helper `DBUnitHelper` los gestiona automáticamente, cargándolos antes de cada test y verificando el estado resultante al finalizar. Su contenido debe reflejar exactamente lo definido en las clases dataset Java. Estos archivos residen en `src/test/resources/datasets`.

- **Matchers Hamcrest personalizados**: cada entidad debe contar con su matcher específico para comparaciones durante los tests. En lugar de depender de `equals`, estos matchers comparan basándose en propiedades clave de negocio, generando mensajes de error más descriptivos que indican exactamente qué propiedad difiere entre objetos esperados y reales. La clase base `IsEqualsToEntity` facilita la creación de nuevos matchers mediante comparación de propiedades.

## 7.4 Objetivos de cobertura

Se establece como meta una cobertura mínima del **70%** en líneas y ramas de código de lógica de negocio. Perseguir el 100% no siempre aporta valor real, pero sí es fundamental cubrir todos los caminos críticos y casos de error. Los informes JaCoCo generados pueden ayudar a identificar zonas sin cobertura que podrían requerir atención.

## 7.5 Ejecución de tests

Antes de ejecutar los tests, es necesario configurar la ubicación de WildFly siguiendo las instrucciones de la [sección 4.2.3](#423-definición-de-la-propiedad-jbosshome) sobre la definición de la propiedad `jboss.home`. Esta configuración es imprescindible para que los tests de integración con Arquillian funcionen correctamente.

### 7.5.1 Ejecución de tests mediante Gradle

Gradle localiza y ejecuta automáticamente todos los tests del proyecto. Para lanzar la suite completa basta con invocar:

```bash
gradle test
```

O alternativamente usando el wrapper de Gradle:

```bash
./gradlew test
```

Ambos comandos ejecutarán todos los tests encontrados en los módulos y generarán automáticamente los informes de cobertura individuales para cada módulo.

Para obtener un informe de cobertura agregado que consolide los resultados de todos los módulos en un único reporte, puede utilizarse el siguiente comando:

```bash
./gradlew jacocoAggregateReport
```

### 7.5.2 Ejecución de tests desde IntelliJ IDEA

IntelliJ IDEA integra de forma nativa la ejecución de pruebas sin requerir configuración adicional. Una vez definida la propiedad `jboss.home`, la ejecución de los tests resulta sencilla e intuitiva.

Es posible ejecutar un método de prueba individual haciendo clic derecho sobre él y seleccionando la opción `Run` correspondiente, ejecutar una clase completa de tests mediante el mismo procedimiento sobre la clase, o bien lanzar todas las pruebas de un paquete específico haciendo clic derecho sobre dicho paquete.

## 7.6 Interpretación de resultados

Cada ejecución genera informes en dos formatos:

- **Informes JUnit**: documentan el resultado de cada test individual. Gradle los almacena en `<module>/build/test-results/test` en formato XML y genera reportes HTML en `<module>/build/reports/tests/test/index.html` que pueden visualizarse directamente en cualquier navegador.

- **Informes JaCoCo individuales**: cada módulo genera su propio informe de cobertura en `<module>/build/reports/jacoco/test/html/index.html`. Estos informes muestran qué líneas de código y bifurcaciones se han ejecutado durante las pruebas de ese módulo.

- **Informe JaCoCo agregado**: al ejecutar la tarea `jacocoRootReport`, se genera un informe consolidado en `build/reports/jacoco/jacocoRootReport/html/index.html` que presenta la cobertura global del proyecto. Este informe es especialmente útil para obtener una visión completa del estado de las pruebas. La ruta exacta del informe se muestra en la consola al finalizar su generación.

Cuando se ejecutan tests directamente desde IntelliJ IDEA, la información equivalente a los informes JUnit aparece inmediatamente en la vista de ejecución de tests del IDE, mostrando resultados en tiempo real con indicadores visuales de éxito o fallo.

# 8. Guía de estilo

Mantener un estilo consistente en todo el proyecto mejora la legibilidad del código y reduce la carga cognitiva al moverse entre diferentes partes de la base de código. Esta sección establece las convenciones que deben seguirse durante el desarrollo.

## 8.1 Código fuente

Para uniformizar el código fuente deben respetarse las siguientes normas:

- **Idioma**: todo el código debe desarrollarse en inglés. Esto incluye nombres de variables, métodos, clases, comentarios y documentación. Se deben utilizar términos claros y descriptivos que comuniquen efectivamente el propósito del código.
- **Convenciones de nombres**: se seguirán rigurosamente las convenciones estándar de Java. Las clases utilizan PascalCase, los métodos y variables camelCase, las constantes UPPER_SNAKE_CASE, y los paquetes minúsculas con puntos como separadores. Los nombres deben ser descriptivos y comunicar claramente su propósito.
- **Formato de código**: el código debe estar formateado siguiendo preferiblemente la [Guía de Estilo para Java de Google](https://google.github.io/styleguide/javaguide.html) o, al menos, utilizando el formato de código del IDE. La mayoría de IDEs permiten formatear automáticamente mediante atajos de teclado: en IntelliJ IDEA se usa `Ctrl+Alt+L` en Windows/Linux o `Cmd+Option+L` en macOS. Se recomienda configurar el IDE para formatear al guardar, asegurando consistencia sin esfuerzo manual.
- **Estructura de métodos**: los métodos deben mantenerse enfocados y relativamente cortos. Métodos que excedan 50 líneas a menudo indican que están haciendo demasiado y deberían descomponerse en métodos más pequeños y enfocados. Esto mejora la testeabilidad, legibilidad y mantenibilidad. La lógica compleja debe extraerse a métodos privados bien nombrados que hagan que el método de nivel superior se lea como una narrativa coherente.
- **Comentarios**: debe evitarse **completamente** el código comentado. Si algún código ya no es necesario, debe eliminarse. El sistema de control de versiones preserva el historial, por lo que siempre es posible recuperar código antiguo si fuera necesario. El código comentado genera confusión sobre si podría necesitarse en el futuro y desordena la base de código. Los comentarios en el código deben minimizarse en la medida de lo posible, priorizando código autoexplicativo.
- **Documentación**: todas las clases públicas deben incluir documentación Javadoc que describa su propósito y responsabilidades. Los métodos públicos con comportamiento no obvio deben incluir Javadoc describiendo parámetros, valores de retorno y excepciones lanzadas. Sin embargo, debe evitarse documentación redundante que meramente repita lo que el código expresa claramente. Por ejemplo, no escribir "Devuelve el nombre" para un método llamado `getName()`. Se recomienda verificar que la documentación es correcta utilizando el comando `gradle javadoc`, que generará la documentación en formato HTML y fallará si encuentra algún error.
- **Capa REST**: al trabajar con recursos REST, deben seguirse los principios de diseño RESTful. Las rutas de los recursos deben usar nombres en plural para representar colecciones (por ejemplo, `/users`, `/pets`). Usar los métodos HTTP apropiados (GET para recuperación, POST para creación, PUT para actualizaciones, DELETE para eliminación), devolver códigos de estado HTTP significativos (200 OK, 201 Created, 400 Bad Request, 404 Not Found, 403 Forbidden, etc.), y diseñar rutas URL que representen jerarquías de recursos. Las respuestas de error deben incluir mensajes útiles en el cuerpo de la respuesta. Cuando se implementen paginaciones, los índices de página deben comenzar en 0 siguiendo la convención estándar de la mayoría de APIs REST.

## 8.2 Mapeo entre entidades y DTOs

En el caso de utilizar DTOs para transferir datos entre capas, es importante que exista un mapeo claro entre las entidades y los DTOs. Para ello, se usará la siguiente convención:

Los DTOs serán clases ubicadas en el paquete `dtos` dentro del paquete del módulo correspondiente. Estos se usarán en la capa Service para transferir datos de entrada y salida de los métodos de los EJBs.

El nombre de los DTOs será el mismo que el de la entidad correspondiente, pero con el sufijo `ResponseDto` o `RequestDto`, según el caso. Por ejemplo, el DTO de respuesta correspondiente a la entidad `Story` se llamará `StoryResponseDto` y el DTO de petición se llamará `StoryRequestDto`.

Las clases encargadas de realizar el mapeo entre entidades y DTOs se llamarán `<EntityName>Mapper`. Por ejemplo, la clase encargada de mapear entre la entidad `Story` y sus DTOs se llamará `StoryMapper`.

## 8.3 Control de versiones

El desarrollo en este proyecto se basa en el principio de integración continua y frecuente. Las siguientes pautas garantizan que el código compartido mantenga siempre un estado funcional:

**Contenido de los commits**: cada commit debe representar un cambio completo que no rompa la compilación del proyecto. El código incluido debe estar correctamente probado según lo descrito en la sección 7. Antes de realizar un commit, conviene revisar los informes de tests y cobertura para asegurar que todo funciona como se espera.

**Formato de los commits**: el formato deberá respetar las siguientes normas:

- Redacción en inglés.
- Líneas limitadas a 80 caracteres.
- Primera línea descriptiva del cambio:
   - Si está relacionado con alguna tarea concreta, debe comenzar con el identificador de la tarea (p.ej. "tsk1: Add...").
   - Si está relacionado con varias tareas, sus números se separarán con un guión (p.ej. "tsk1-2-13: Fix...").
   - Debe estar redactada en **imperativo** (p.ej. *Add...*, *Improve...*, *Modify...*, etc.).
   - No debe llevar punto final.
- Cuerpo del commit:
   - Con una línea vacía de separación respecto a la primera línea.
   - Debe escribirse un texto que explique claramente el trabajo realizado en el commit.
   - Los párrafos deben finalizar con punto.

**Frecuencia de commits**: los commits deben hacerse en pequeños pasos para que la frecuencia sea alta. Para ello es recomendable desarrollar de forma ordenada, atacando partes concretas. Se espera al menos 2-3 commits por desarrollador cada semana, distribuidos uniformemente a lo largo del periodo. Concentrar todos los commits al final de la semana perjudica la integración continua.

**Frecuencia de push**: cada commit debe ir acompañado inmediatamente de su correspondiente push.

## 8.4 Material adicional

El módulo `additional-material` agrupa recursos auxiliares necesarios para el despliegue y desarrollo que no forman parte de los artefactos desplegables. Su contenido requiere mantenimiento en los siguientes escenarios:

**Modificaciones en el esquema de base de datos**: cualquier cambio en las entidades del módulo domain (creación, modificación o eliminación) obliga a actualizar el script SQL ubicado en el subdirectorio `db`. El fichero `petstore-mysql.sql` debe contener las sentencias necesarias para inicializar completamente el sistema: creación de la base de datos, definición de tablas, configuración del usuario de aplicación con sus permisos, e inserción de datos de prueba. Un administrador debería poder ejecutar únicamente este script contra un servidor MySQL limpio y tener la aplicación lista para usar inmediatamente.
