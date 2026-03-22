# Lyra — Sistema de Gestión de Hotel de Cabañas Rurales

![Estado](https://img.shields.io/badge/estado-en%20desarrollo-yellow)
![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)
![Supabase](https://img.shields.io/badge/Supabase-cloud-3ECF8E?logo=supabase)
![JDBC](https://img.shields.io/badge/JDBC-Driver-lightgrey)
![XML](https://img.shields.io/badge/XML%20%2B%20XSD-validado-green)
![Git](https://img.shields.io/badge/Git-control%20de%20versiones-red?logo=git)
![Licencia](https://img.shields.io/badge/licencia-MIT-brightgreen)

---

## Descripción

**Lyra** es una aplicación de escritorio desarrollada en Java para la gestión integral de un hotel de cabañas rurales. Permite administrar reservas, clientes, cabañas y servicios adicionales desde una interfaz sencilla y eficiente, con persistencia de datos en PostgreSQL gestionado con Supabase y exportación/importación de información mediante ficheros XML validados con XSD.

El proyecto ha sido desarrollado como proyecto intermodular de **1.º DAM (Desarrollo de Aplicaciones Multiplataforma)**, integrando los conocimientos de los módulos de Programación, Bases de Datos, Lenguajes de Marcas, Sistemas, MPO y Entornos de Desarrollo.

---

## Problema que resuelve

Los establecimientos rurales de pequeño y mediano tamaño carecen habitualmente de herramientas de gestión adaptadas a sus necesidades. Lyra ofrece una solución centralizada que permite:

- Registrar y consultar clientes con sus datos de contacto.
- Gestionar el catálogo de cabañas disponibles con sus características y precios.
- Crear, modificar y cancelar reservas, controlando disponibilidad en tiempo real.
- Asociar servicios adicionales (limpieza, desayuno, excursiones, etc.) a cada reserva.
- Exportar e importar datos en formato XML para copias de seguridad o intercambio de información.

---

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje principal de desarrollo |
| PostgreSQL 15 | Base de datos relacional (cloud via Supabase) |
| Supabase | Plataforma cloud para PostgreSQL |
| JDBC | Conexión Java – PostgreSQL |
| XML + XSD | Exportación de datos y validación de esquemas |
| Git + GitHub | Control de versiones y repositorio remoto |
| IntelliJ IDEA | Entorno de desarrollo principal |

---

## Estructura del repositorio

```
ProyectoIntermodular/
├── src/                    # Código fuente Java
│   ├── model/              # Clases de dominio (entidades)
│   ├── service/            # Lógica de negocio
│   ├── controller/         # Controladores de flujo
│   ├── utils/              # Clases de utilidad y helpers
│   └── database/           # Gestión de conexión y DAO
├── sql/                    # Scripts SQL (DDL y datos de prueba)
├── xml/                    # Ficheros XML y esquemas XSD
├── diagrams/               # Diagramas UML, ER y de casos de uso
├── docs/                   # Documentación del proyecto
│   └── sistemas/           # Documentación del módulo de Sistemas
├── .gitignore              # Ficheros y carpetas ignorados por Git
└── README.md               # Este fichero
```

---

## Módulos académicos integrados

| Módulo | Aportación al proyecto |
|---|---|
| Programación | Desarrollo de la aplicación en Java (POO, colecciones, excepciones) |
| Bases de Datos | Diseño del modelo relacional, SQL y acceso con JDBC |
| Lenguajes de Marcas | Generación y validación de ficheros XML con XSD |
| Sistemas | Configuración del entorno, scripts de despliegue y administración |
| MPO | Documentación técnica y manual de usuario |
| Entornos de Desarrollo | Control de versiones con Git, uso de IDE y depuración |

---

## Instalación y ejecución

> **Requisitos previos:** Java 17+, cuenta en Supabase (o acceso al proyecto configurado), Maven o configuración manual del classpath.

### 1. Clonar el repositorio

```bash
git clone https://github.com/<usuario>/ProyectoIntermodular.git
cd ProyectoIntermodular
```

### 2. Configurar la base de datos

Ejecuta los scripts SQL contra tu instancia de Supabase (o cualquier servidor PostgreSQL compatible):

```bash
# Importar el esquema y los datos de prueba (requiere psql)
psql -h <host-supabase> -U postgres -d postgres -f sql/schema.sql
psql -h <host-supabase> -U postgres -d postgres -f sql/data.sql
```

### 3. Configurar la conexión

Crea el fichero `src/database/db.properties` con tus credenciales de Supabase:

```properties
db.url=jdbc:postgresql://<host-supabase>:5432/postgres
db.user=postgres
db.password=<contraseña>
```

> Este fichero está excluido del control de versiones por seguridad (ver `.gitignore`).

### 4. Compilar y ejecutar

```bash
# (Instrucciones de compilación y ejecución — pendiente de completar)
```

---

## Estado del proyecto

**En desarrollo** — Primera iteración en curso.

- [x] Estructura inicial del repositorio
- [x] Diseño del modelo entidad-relación
- [ ] Scripts SQL (DDL)
- [ ] Implementación de entidades Java
- [ ] Capa de acceso a datos (DAO / JDBC)
- [ ] Lógica de negocio (servicios)
- [ ] Gestión de XML + XSD
- [ ] Pruebas y documentación final

---

## Autores

Proyecto desarrollado por JUAN BERMÚDEZ PINO. 

---

## Licencia

Este proyecto se distribuye bajo la licencia [MIT](https://opensource.org/licenses/MIT).
