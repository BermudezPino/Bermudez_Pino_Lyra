# Lyra — Sistema de Gestión de Hotel de Cabañas Rurales

![Estado](https://img.shields.io/badge/estado-funcional-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)
![Supabase](https://img.shields.io/badge/Supabase-cloud-3ECF8E?logo=supabase)
![Maven](https://img.shields.io/badge/Maven-3.9+-red?logo=apachemaven)
![XML](https://img.shields.io/badge/XML%20%2B%20XSD-validado-green)
![Licencia](https://img.shields.io/badge/licencia-MIT-brightgreen)

---

## Descripción

**Lyra** es una aplicación de escritorio desarrollada en Java 17 con interfaz gráfica JavaFX para la gestión integral de un hotel de cabañas rurales. Permite administrar reservas, clientes, cabañas, empleados y servicios adicionales desde una interfaz visual, con persistencia de datos en PostgreSQL 15 gestionado con Supabase y exportación de información mediante ficheros XML validados con XSD.

El proyecto ha sido desarrollado como proyecto intermodular de **1.º DAM (Desarrollo de Aplicaciones Multiplataforma)**, integrando los módulos de Programación (0485), Bases de Datos (0484), Lenguajes de Marcas (0373), Sistemas (0483), MPO y Entornos de Desarrollo (0487).

---

## Problema que resuelve

Los establecimientos rurales de pequeño y mediano tamaño carecen habitualmente de herramientas de gestión adaptadas a sus necesidades. Lyra ofrece una solución centralizada que permite:

- Registrar y consultar clientes con sus datos de contacto.
- Gestionar el catálogo de cabañas disponibles con sus características y precios.
- Crear, modificar y cancelar reservas, controlando disponibilidad en tiempo real.
- Asociar servicios adicionales (limpieza, desayuno, excursiones, etc.) a cada reserva.
- Calcular automáticamente el precio total: días × precio/noche + servicios contratados.
- Exportar reservas completas a formato XML validado contra un esquema XSD.
- Registrar qué empleados gestionaron cada reserva y en qué rol.

---

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 | Lenguaje principal de desarrollo |
| JavaFX | 21 | Interfaz gráfica de usuario (GUI con FXML) |
| PostgreSQL | 15 | Base de datos relacional (cloud via Supabase) |
| Supabase | — | Plataforma cloud para PostgreSQL |
| JDBC | puro (sin ORM) | Conexión Java – PostgreSQL |
| Lombok | 1.18.36 | Reducción de código repetitivo (POJOs) |
| XML + XSD | — | Exportación de datos y validación de esquemas |
| Apache Maven | 3.9+ | Gestión de dependencias y compilación |
| Git + GitHub | — | Control de versiones y repositorio remoto |
| IntelliJ IDEA | — | Entorno de desarrollo principal |

---

## Estructura del repositorio

```
Bermudez_Pino_Lyra/
├── src/
│   └── main/
│       ├── java/com/lyra/
│       │   ├── Launcher.java           # Punto de entrada (ejecutado por Maven/JAR)
│       │   ├── Main.java               # Aplicación JavaFX
│       │   ├── model/                  # Clases de dominio (POJOs con Lombok)
│       │   ├── service/                # Lógica de negocio
│       │   ├── controller/             # Controladores JavaFX (FXML)
│       │   ├── utils/                  # XmlExporter, XmlValidator
│       │   └── database/               # DatabaseConnection (Singleton) y DAOs
│       └── resources/
│           ├── fxml/                   # Vistas de la interfaz gráfica
│           ├── css/                    # Estilos de la aplicación
│           ├── xml/                    # Esquema XSD de exportación
│           └── database.properties     # Credenciales (NO subir a GitHub — ver .gitignore)
├── sql/
│   ├── schema.sql                      # DDL completo de PostgreSQL
│   ├── data.sql                        # Datos de ejemplo
│   └── queries.sql                     # Consultas JDBC documentadas
├── xml/                                # Exportaciones XML generadas
├── diagrams/                           # Diagramas E/R y relacionales
├── docs/
│   ├── modelo_relacional.md            # Modelo relacional documentado
│   └── sistemas/                       # Informe técnico (módulo 0483)
├── pom.xml
└── README.md
```

---

## Módulos académicos integrados

| Módulo | Aportación al proyecto |
|---|---|
| 0484 Bases de Datos | Modelo relacional, scripts SQL DDL/DML, consultas con JOIN |
| 0485 Programación | Java + JDBC puro + CRUD completo + arquitectura en capas |
| 0373 Lenguajes de Marcas | Generación y validación de XML con XSD en tiempo de ejecución |
| 0483 Sistemas Informáticos | Informe del entorno de ejecución y guía de instalación |
| 0487 Entornos de Desarrollo | Git, historial de commits distribuido, README profesional |
| MPO | Cálculo automático del precio total, calidad de arquitectura |

---

## Requisitos previos

- **Java 17** JDK — se recomienda [Temurin de Adoptium](https://adoptium.net)
- **Apache Maven 3.9+**
- **Git**
- Credenciales de acceso a la base de datos Supabase (facilitadas por el administrador del proyecto)

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/BermudezPino/Bermudez_Pino_Lyra.git
cd Bermudez_Pino_Lyra
```

### 2. Configurar la base de datos

Ejecuta los scripts SQL en orden contra tu instancia de PostgreSQL en Supabase:

```bash
psql -h <host-supabase> -U postgres -d postgres -f sql/schema.sql
psql -h <host-supabase> -U postgres -d postgres -f sql/data.sql
```

### 3. Configurar la conexión

Copia el fichero de ejemplo y rellena tus credenciales:

```bash
cp src/main/resources/database.properties.example src/main/resources/database.properties
```

Edita `src/main/resources/database.properties`:

```properties
db.url=jdbc:postgresql://<host-supabase>:5432/postgres
db.user=postgres
db.password=<contraseña>
```

> Este fichero está excluido del control de versiones por seguridad (ver `.gitignore`).

### 4. Compilar el proyecto

```bash
mvn clean package
```

Maven descarga automáticamente las dependencias (PostgreSQL JDBC, Lombok, JavaFX 21).

### 5. Ejecutar la aplicación

```bash
mvn javafx:run
```

La ventana principal de Lyra se abre con un menú lateral para acceder a Cabañas, Clientes, Reservas, Servicios y Empleados.

---

## Resolución de problemas

| Problema | Causa probable | Solución |
|---|---|---|
| Error de conexión a la BD | Credenciales incorrectas o archivo no creado | Verificar `src/main/resources/database.properties` |
| `java -version` no reconocido | JDK no añadido al PATH | Reconfigurar la variable de entorno PATH |
| Error al compilar con Maven | Dependencias no descargadas | Ejecutar `mvn clean install` con conexión a internet |
| La ventana no se abre | JavaFX no cargado | Usar `mvn javafx:run` en lugar de `java -jar` |

---

## Estado del proyecto

**Funcional** — implementación principal completa.

- [x] Estructura inicial del repositorio y configuración Maven
- [x] Diseño del modelo entidad-relación
- [x] Scripts SQL: DDL (`schema.sql`), datos (`data.sql`), consultas (`queries.sql`)
- [x] Entidades Java con Lombok (model/)
- [x] Capa de acceso a datos — DAOs con JDBC puro
- [x] Lógica de negocio — ReservaService con cálculo automático de precio total
- [x] Interfaz gráfica JavaFX con vistas FXML y CSS
- [x] Exportación XML + validación XSD en tiempo de ejecución
- [x] Diagramas E/R finales en `/diagrams/`
- [x] Capturas de pantalla del sistema en funcionamiento
- [ ] Entrega final (3 de mayo de 2026)

---

## Control de versiones

El proyecto sigue el formato **Conventional Commits** para mantener un historial de cambios legible y ordenado.

Los prefijos que se usan son:

| Prefijo | Cuándo usarlo |
|---|---|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de un error |
| `docs` | Cambios en documentación |
| `build` | Cambios en Maven, dependencias o configuración |
| `refactor` | Mejora de código sin cambiar comportamiento |
| `chore` | Tareas de mantenimiento (limpieza, renombrar, etc.) |

Ejemplos:

```
feat: añadir exportación de reservas a XML
fix: corregir cálculo del precio total con servicios extra
docs: actualizar README con instrucciones de instalación
```

---

## Autores

Proyecto desarrollado por **Juan Bermúdez Pino** | 1.º DAM | Prometeo by The Power (FP Oficial)

---

## Licencia

Este proyecto se distribuye bajo la licencia [MIT](https://opensource.org/licenses/MIT).
