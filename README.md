# Pet Store

Este proyecto constituye una aplicación empresarial modular basada en **Jakarta EE 10**, diseñada para gestionar mascotas de forma integral. La arquitectura implementa un modelo multicapa completo que abarca persistencia JPA, lógica de negocio con EJBs, servicios RESTful (JAX-RS), interfaz de usuario web (JSF/Jakarta Faces) y un sistema de seguridad robusto.

La aplicación permite administrar usuarios y sus respectivas mascotas, aplicando reglas de negocio que determinan los permisos de visualización y modificación de información según el rol de cada usuario.

## 🛠 Stack tecnológico

El proyecto se apoya en estándares de la industria que garantizan mantenibilidad, escalabilidad y rendimiento:

* **Core:** Java 21, Jakarta EE 10
* **Web & API:** Jakarta Faces (JSF), Jakarta REST (JAX-RS)
* **Negocio & Datos:** EJB, JPA, MySQL 9+
* **Infraestructura:** WildFly, Docker
* **Calidad & Testing:** JUnit 5, Arquillian, DBUnit, SonarQube

## 📦 Arquitectura modular

El código está organizado en un **monorepo Gradle** estructurado en módulos independientes que promueven la separación de responsabilidades:

| Módulo     | Responsabilidad                                                      |
|------------|----------------------------------------------------------------------|
| `domain`   | Definición de entidades JPA y reglas de validación del modelo       |
| `service`  | Lógica de negocio (EJB), gestión transaccional y control de acceso  |
| `rest`     | API RESTful (JAX-RS) que expone los servicios mediante endpoints    |
| `jsf`      | Interfaz de usuario web construida con Jakarta Faces                |
| `security` | Configuración de autenticación y algoritmos de hashing              |
| `tests`    | Utilidades compartidas para pruebas de integración                  |
| `ear`      | Empaquetado final de la aplicación para despliegue en servidor      |

## 🚀 Acceso a la aplicación

Una vez desplegada la aplicación (ya sea en entorno local o servidor), los puntos de acceso disponibles son:

* **Interfaz Web:** `http://localhost:8080/pet-store/jsf/`
* **API REST:** `http://localhost:8080/pet-store/rest/`

### Credenciales de prueba

El sistema incluye datos de prueba para facilitar la exploración:

| Rol               | Usuario | Contraseña |
|-------------------|---------|------------|
| **Administrador** | `jose`  | `josepass` |
| **Propietario**   | `ana`   | `anapass`  |

---

## 💻 Guía para desarrolladores

La documentación técnica completa sobre arquitectura, configuración del entorno de desarrollo, flujo de trabajo y estándares de código se encuentra disponible en el [documento de contribución](CONTRIBUTING.md).

Este documento incluye:

* Configuración del entorno de desarrollo (Java, Docker, WildFly)
* Análisis detallado de la arquitectura modular y sus interacciones
* Estrategias de versionado (Git Flow) y metodologías de testing
* Convenciones de código y mejores prácticas del proyecto
