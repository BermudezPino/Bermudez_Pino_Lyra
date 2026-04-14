# Línea Temporal de Desarrollo — Proyecto Lyra

**Alumno:** Juan | **Curso:** 1.º DAM | **Fecha límite:** 3 de mayo de 2026

---

## Resumen de semanas

| Semana | Fechas aprox. | Foco principal |
|--------|---------------|----------------|
| 1 | 16–22 mar | Estructura del proyecto, base de datos E/R |
| 2 | 23–29 mar | SQL scripts, modelo relacional, conexión JDBC |
| 3 | 30 mar – 5 abr | Modelos Java (POJOs), DAOs básicos |
| 4 | 6–12 abr | CRUD completo, servicios de negocio |
| 5 | 13–19 abr | Menú consola, flujo de usuario |
| 6 | 20–26 abr | XML + XSD, exportación de reservas |
| 7 | 27 abr – 3 may | Pulido final, informe técnico, entrega |

---

## Semana 1 — Estructura y diseño de base de datos
**16–22 de marzo**

**Módulos:** 0484 Bases de Datos · 0487 Entornos de Desarrollo

### Tareas
- [x] Inicializar repositorio Git y crear `.gitignore`
- [ ] Crear `README.md` con descripción del proyecto, stack y estructura
- [ ] Diseñar diagrama Entidad-Relación (Cabaña, Cliente, Reserva, ServicioExtra, Empleado, ReservaServicio)
- [ ] Definir modelo relacional a partir del E/R
- [ ] Configurar proyecto Maven con `pom.xml` (dependencias: PostgreSQL JDBC, Lombok)

### Commits sugeridos
| Día | Mensaje de commit |
|-----|-------------------|
| Lun 16 | `docs: añadir README inicial y .gitignore` |
| Mié 18 | `feat(db): añadir diagrama E/R inicial del sistema` |
| Vie 20 | `feat(db): añadir modelo relacional normalizado` |
| Dom 22 | `build: configurar proyecto Maven con dependencias base` |

---

## Semana 2 — Scripts SQL y conexión a Supabase
**23–29 de marzo**

**Módulos:** 0484 Bases de Datos · 0485 Programación

### Tareas
- [ ] Escribir script `schema.sql` con CREATE TABLE para todas las entidades
- [ ] Definir claves primarias, foráneas y restricciones (NOT NULL, UNIQUE, CHECK)
- [ ] Insertar datos de prueba con `data.sql` (mínimo 5 cabañas, 10 clientes, 15 reservas)
- [ ] Crear clase `DatabaseConnection.java` con singleton JDBC
- [ ] Verificar conexión con Supabase desde IntelliJ

### Commits sugeridos
| Día | Mensaje de commit |
|-----|-------------------|
| Lun 23 | `feat(db): añadir script schema.sql con todas las tablas` |
| Mié 25 | `feat(db): añadir restricciones y relaciones en schema.sql` |
| Jue 26 | `feat(db): añadir datos de prueba en data.sql` |
| Sáb 28 | `feat(database): añadir clase DatabaseConnection con singleton JDBC` |

---

## Semana 3 — Modelos Java y DAOs básicos
**30 de marzo – 5 de abril**

**Módulos:** 0485 Programación · MPO

### Tareas
- [ ] Crear POJOs en `com.lyra.model`: `Cabana`, `Cliente`, `Reserva`, `ServicioExtra`, `Empleado`, `ReservaServicio`
- [ ] Añadir anotaciones Lombok (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- [ ] Crear interfaces DAO genéricas (`findById`, `findAll`, `save`, `update`, `delete`)
- [ ] Implementar `CabanaDAO` y `ClienteDAO` con consultas SQL básicas
- [ ] Escribir consultas SQL específicas del módulo 0484 (mínimo 5 consultas documentadas)

### Commits sugeridos
| Día | Mensaje de commit |
|-----|-------------------|
| Lun 30 | `feat(model): añadir entidades Cabana y Cliente con Lombok` |
| Mar 31 | `feat(model): añadir entidades Reserva, ServicioExtra y Empleado` |
| Jue 2 | `feat(dao): añadir interfaz DAO genérica y CabanaDAO` |
| Sáb 4 | `feat(dao): añadir ClienteDAO con operaciones CRUD` |

---

## Semana 4 — CRUD completo y lógica de negocio
**6–12 de abril**

**Módulos:** 0485 Programación · MPO

### Tareas
- [ ] Implementar `ReservaDAO`, `ServicioExtraDAO`, `EmpleadoDAO`, `ReservaServicioDAO`
- [ ] Crear `ReservaService` con cálculo automático de precio total (días × precio/noche + servicios)
- [ ] Implementar cambio de estado de reservas: `PENDIENTE → CONFIRMADA → ACTIVA → COMPLETADA/CANCELADA`
- [ ] Añadir validaciones de negocio (fechas solapadas, cabaña disponible, cliente existe)
- [ ] Escribir consultas SQL avanzadas: reservas por cliente, ingresos por cabaña, ocupación mensual

### Commits sugeridos
| Día | Mensaje de commit |
|-----|-------------------|
| Lun 6 | `feat(dao): añadir ReservaDAO con búsqueda por fechas` |
| Mar 7 | `feat(dao): añadir ServicioExtraDAO y ReservaServicioDAO` |
| Jue 9 | `feat(service): añadir ReservaService con cálculo de precio total` |
| Vie 10 | `feat(service): añadir validaciones de disponibilidad y estados` |
| Dom 12 | `feat(db): añadir consultas SQL avanzadas para informes` |

---

## Semana 5 — Menú consola y flujo de usuario
**13–19 de abril**

**Módulos:** 0485 Programación · MPO

### Tareas
- [ ] Crear `MainMenu.java` en `com.lyra.controller` con menú principal por consola
- [ ] Implementar submenús: Gestión de Cabañas, Gestión de Clientes, Gestión de Reservas
- [ ] Añadir flujo completo: crear reserva, añadir servicios, confirmar, cancelar
- [ ] Crear `ConsoleUtils.java` en `utils` para leer entradas del usuario con validación
- [ ] Añadir formateo de salida: tablas en consola, fechas en formato `dd/MM/yyyy`

### Commits sugeridos
| Día | Mensaje de commit |
|-----|-------------------|
| Lun 13 | `feat(controller): añadir menú principal por consola` |
| Mié 15 | `feat(controller): añadir submenús de cabañas y clientes` |
| Jue 16 | `feat(controller): añadir flujo completo de gestión de reservas` |
| Sáb 18 | `feat(utils): añadir utilidades de consola y formateo de fechas` |

---

## Semana 6 — XML + XSD y exportación
**20–26 de abril**

**Módulos:** 0373 Lenguajes de Marcas · MPO

### Tareas
- [ ] Definir esquema `reserva.xsd` en `src/main/resources/xml/` con validación completa
- [ ] Crear `XmlExporter.java` en `utils` para generar XML de una reserva con sus servicios
- [ ] Validar el XML generado contra el XSD en tiempo de ejecución (usando `javax.xml.validation`)
- [ ] Integrar exportación en el menú consola (opción "Exportar reserva a XML")
- [ ] Generar al menos un XML de ejemplo para documentación

### Commits sugeridos
| Día | Mensaje de commit |
|-----|-------------------|
| Lun 20 | `feat(xml): añadir esquema XSD para exportación de reservas` |
| Mié 22 | `feat(xml): añadir XmlExporter con generación de reserva completa` |
| Jue 23 | `feat(xml): añadir validación XSD en tiempo de ejecución` |
| Sáb 25 | `feat(controller): integrar exportación XML en el menú de reservas` |

---

## Semana 7 — Pulido final y entrega
**27 de abril – 3 de mayo**

**Módulos:** 0483 Sistemas Informáticos · 0487 Entornos de Desarrollo · Todos

### Tareas
- [ ] Redactar informe técnico del entorno: SO, JDK, IntelliJ, PostgreSQL, Supabase, Maven
- [ ] Revisar y completar `README.md`: instrucciones de instalación, configuración de Supabase, ejecución
- [ ] Revisar todos los commits: mensajes claros, bien distribuidos en el tiempo
- [ ] Comprobar que el proyecto compila y ejecuta desde cero (sin datos locales de sesión)
- [ ] Revisar calidad del código: sin código muerto, constantes bien nombradas, try-with-resources en JDBC
- [ ] Entrega final antes del domingo 3 de mayo a las 23:59

### Commits sugeridos
| Día | Mensaje de commit |
|-----|-------------------|
| Lun 27 | `docs: añadir informe técnico del entorno de ejecución` |
| Mar 28 | `refactor: limpiar código y mejorar nombres de variables` |
| Jue 30 | `docs: actualizar README con instrucciones de instalación y uso` |
| Sáb 2 | `chore: revisión final antes de entrega` |

---

## Hitos por módulo

| Módulo | Hito | Semana |
|--------|------|--------|
| 0484 Bases de Datos | Diagrama E/R + modelo relacional publicados | Semana 1 |
| 0484 Bases de Datos | Scripts SQL completos + consultas documentadas | Semana 4 |
| 0487 Entornos de Desarrollo | README profesional + historial de commits distribuido | Semana 1–7 |
| 0485 Programación | CRUD completo funcionando con JDBC puro | Semana 4 |
| 0485 Programación | Menú consola operativo con flujo completo | Semana 5 |
| 0373 Lenguajes de Marcas | XSD definido + XML generado y validado | Semana 6 |
| 0483 Sistemas Informáticos | Informe técnico del entorno redactado | Semana 7 |
| MPO | Cálculo de precio total + calidad arquitectural | Semana 4–5 |

---

## Notas importantes

- **Commits distribuidos:** nunca agrupar más de 3-4 commits en el mismo día. El historial debe reflejar trabajo progresivo.
- **JDBC puro:** no usar Hibernate ni ningún ORM. Todas las consultas SQL van en mayúsculas dentro del código Java.
- **try-with-resources:** obligatorio en todas las conexiones y statements JDBC.
- **Supabase:** la conexión requiere la URL y credenciales del proyecto en las variables de entorno o en un archivo de configuración fuera del repositorio.
- **XSD real:** la validación debe hacerse en tiempo de ejecución, no solo tener el archivo XSD sin usarlo.
