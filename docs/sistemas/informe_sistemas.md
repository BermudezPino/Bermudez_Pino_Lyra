# Informe Técnico — Sistemas Informáticos (Módulo 0483)

**Proyecto:** Lyra — Sistema de Gestión de Hotel de Cabañas Rurales
**Alumno:** Juan
**Curso:** 1.º DAM — Prometeo by The Power (FP Oficial)
**Fecha:** Marzo 2026

---

## 1. Tipo de sistema

Lyra es una **aplicación de escritorio** destinada a ejecutarse en el **PC del recepcionista/administrador** de un hotel rural de pequeño tamaño. No se trata de un servidor ni de una máquina virtual.

| Característica | Descripción |
|---|---|
| Tipo de máquina | PC de usuario (escritorio o portátil) |
| Modalidad de uso | Monousuario, uso local |
| Base de datos | Remota en Supabase (PostgreSQL 15 en la nube) |
| Interfaz | JavaFX 21 (aplicación de escritorio con interfaz gráfica) |

**Justificación:** La aplicación está diseñada para un único operador que gestiona las reservas, clientes y cabañas del hotel desde un equipo local mediante una interfaz gráfica JavaFX con vistas FXML. La base de datos reside en Supabase (cloud), por lo que el equipo local no necesita alojar ningún servidor de base de datos; solo requiere conexión a internet para comunicarse con el servicio remoto.

---

## 2. Requisitos de hardware

### CPU

| Nivel | Especificación |
|---|---|
| Mínima | Intel Core i3 (o AMD equivalente), 2 núcleos, 2.0 GHz |
| Recomendada | Intel Core i5 / i7 (o AMD equivalente), 4 núcleos, 2.5 GHz o superior |

### Memoria RAM

| Nivel | Cantidad |
|---|---|
| Mínima | 4 GB |
| Recomendada | 8 GB |

### Almacenamiento

| Componente | Espacio requerido |
|---|---|
| JDK 17 | ~300 MB |
| Apache Maven 3.9+ | ~100 MB |
| Proyecto Lyra (código fuente + dependencias Maven) | ~100 MB |
| **Total mínimo libre** | **~500 MB** |

> La base de datos está alojada en Supabase (cloud). No se requiere espacio local para la BD.

### Periféricos y conectividad

- Monitor con resolución mínima de **1280 x 720** píxeles
- Teclado y ratón
- **Conexión a internet** (obligatoria para conectar con Supabase)

---

## 3. Sistema operativo

### Sistema principal

| Sistema | Versión | Arquitectura |
|---|---|---|
| Windows | 10 / 11 | 64 bits |

### Sistemas compatibles

| Sistema | Versión |
|---|---|
| Ubuntu | 22.04 LTS o superior |
| macOS | 13 (Ventura) o superior |

**Justificación:** La aplicación está desarrollada en Java 17 con dependencias gestionadas por Maven, lo que la hace multiplataforma por naturaleza. JavaFX 21 (incluido como dependencia en el `pom.xml`) también está disponible para Windows, Linux y macOS. El desarrollo y las pruebas se han realizado sobre **Windows 11 Pro (64 bits)**.

---

## 4. Guía de instalación paso a paso

### Requisitos previos

Antes de instalar la aplicación, es necesario tener disponibles los siguientes componentes:

- Acceso a internet
- Credenciales de la base de datos Supabase (facilitadas por el administrador del proyecto)

---

### Paso 1 — Instalar JDK 17

1. Acceder a [https://adoptium.net](https://adoptium.net)
2. Descargar el instalador de **Temurin JDK 17** para el sistema operativo correspondiente
3. Ejecutar el instalador y seguir los pasos (en Windows, marcar la opción de añadir al PATH)
4. Verificar la instalación abriendo una terminal y ejecutando:

```bash
java -version
```

Salida esperada (ejemplo):

```
openjdk version "17.0.x" ...
```

---

### Paso 2 — Instalar Apache Maven 3.9+

1. Acceder a [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
2. Descargar el archivo binario `.zip` (Windows) o `.tar.gz` (Linux/macOS)
3. Descomprimir en una carpeta de preferencia (por ejemplo, `C:\Program Files\Maven`)
4. Añadir la carpeta `bin/` de Maven a la variable de entorno `PATH`
5. Verificar la instalación:

```bash
mvn -version
```

Salida esperada (ejemplo):

```
Apache Maven 3.9.x ...
Java version: 17.x.x ...
```

---

### Paso 3 — Clonar el repositorio

```bash
git clone https://github.com/BermudezPino/Bermudez_Pino_Lyra
```

Si no se tiene Git instalado, se puede descargar el código fuente directamente desde GitHub como archivo `.zip` y descomprimirlo.

---

### Paso 4 — Configurar las credenciales de Supabase

1. Dentro del proyecto clonado, abrir el archivo:

```
src/main/resources/database.properties
```

2. Rellenar los datos de conexión:

```properties
db.url=jdbc:postgresql://<host-supabase>:5432/postgres
db.user=<usuario>
db.password=<contraseña>
```

> Este archivo **no debe subirse a GitHub**. Está incluido en el `.gitignore` del proyecto.

---

### Paso 5 — Compilar el proyecto

Desde la raíz del proyecto (donde se encuentra el `pom.xml`):

```bash
mvn clean package
```

Maven descargará automáticamente las dependencias definidas en el `pom.xml`:

| Dependencia | Versión |
|---|---|
| PostgreSQL JDBC Driver (`org.postgresql:postgresql`) | 42.7.3 |
| Lombok (`org.projectlombok:lombok`) | 1.18.36 |
| JavaFX Controls (`org.openjfx:javafx-controls`) | 21 |
| JavaFX FXML (`org.openjfx:javafx-fxml`) | 21 |

---

### Paso 6 — Ejecutar la aplicación

**Opción A — Con Maven:**

```bash
mvn javafx:run
```

> **Nota:** La ejecución directa con `java -jar` requiere configuración adicional del módulo JavaFX (`--module-path` y `--add-modules`). Se recomienda usar exclusivamente `mvn javafx:run` como método de ejecución.

---

## 5. Usuarios, permisos y estructura del sistema

### Usuarios del sistema

| Usuario | Rol | Acceso |
|---|---|---|
| Recepcionista / Administrador del hotel | Usuario único | Acceso completo a todas las funciones |

- La aplicación no implementa sistema de autenticación propio. El acceso es local y directo.
- Las credenciales de la base de datos se almacenan en `database.properties` (fichero local, no incluido en el repositorio).

### Estructura de carpetas del proyecto

```
Bermudez_Pino_Lyra/
├── src/
│   └── main/
│       ├── java/com/lyra/
│       │   ├── model/          <- Clases entidad (POJOs)
│       │   ├── service/        <- Lógica de negocio
│       │   ├── controller/     <- Controladores JavaFX (FXML) y flujo de usuario
│       │   ├── utils/          <- Utilidades (XML, validación, formateo)
│       │   └── database/       <- Conexión JDBC y DAOs
│       └── resources/
│           ├── fxml/                 <- Vistas JavaFX (FXML)
│           ├── css/                  <- Estilos de la interfaz
│           ├── xml/                  <- Esquema XSD de validación
│           └── database.properties   <- Credenciales (NO subir a GitHub)
├── docs/
│   └── sistemas/               <- Este informe y capturas
├── sql/                        <- Scripts DDL y DML de PostgreSQL
├── diagrams/                   <- Diagramas E/R y relacionales
├── xml/                        <- Exportaciones XML de reservas
├── pom.xml
└── README.md
```

### Base de datos en Supabase

- El acceso a PostgreSQL 15 es exclusivamente mediante las credenciales definidas en `database.properties`
- El fichero con credenciales no se sube al repositorio (incluido en `.gitignore`)
- Solo el equipo con acceso a ese fichero puede conectarse a la base de datos

---

## 6. Mantenimiento

### JDK y entorno Java

- Revisar y actualizar el JDK 17 cuando se publiquen **parches de seguridad** (aproximadamente cada 6 meses). Descargar desde [https://adoptium.net](https://adoptium.net).
- Verificar que Maven sigue siendo compatible con la versión de JDK instalada tras cada actualización.

### Base de datos Supabase

- El plan gratuito de Supabase tiene **límites de filas activas y conexiones simultáneas**. Revisar periódicamente el uso desde el panel de Supabase ([https://supabase.com/dashboard](https://supabase.com/dashboard)).
- Realizar un **backup mensual** de la base de datos exportando los datos desde el panel de Supabase (`Database → Backups` o exportación manual en SQL).

### Resolución de problemas habituales

| Problema | Causa probable | Solución |
|---|---|---|
| La aplicación no arranca | Falta de conexión a internet | Verificar la conexión de red |
| Error de conexión a la BD | Credenciales incorrectas o caducadas | Revisar `database.properties` |
| `java -version` no reconocido | JDK no añadido al PATH | Reconfigurar la variable de entorno PATH |
| Error al compilar con Maven | Dependencias no descargadas | Ejecutar `mvn clean install` con conexión a internet |

---

## 7. Evidencias

### Capturas de funcionamiento

Las capturas de pantalla que muestran el funcionamiento de la aplicación (ventana principal, gestión de reservas, exportación XML, etc.) se adjuntan en la carpeta:

> Las capturas se añadirán antes de la entrega final (3 de mayo de 2026).

```
docs/sistemas/capturas/
```

### Historial de commits

El repositorio de GitHub muestra el historial completo de commits como evidencia del proceso de desarrollo a lo largo del tiempo:

[https://github.com/BermudezPino/Bermudez_Pino_Lyra](https://github.com/BermudezPino/Bermudez_Pino_Lyra)

Los commits están distribuidos a lo largo del período de desarrollo, reflejando el progreso incremental del proyecto (estructura inicial, scripts SQL, DAOs, lógica de negocio, exportación XML, etc.).

---

*Informe generado para el módulo 0483 — Sistemas Informáticos | 1.º DAM | Prometeo by The Power*
