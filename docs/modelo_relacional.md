# Modelo Relacional — Lyra

Sistema de Gestión de Hotel de Cabañas Rurales
Proyecto Intermodular 1.º DAM · Base de datos PostgreSQL 15

---

## Descripción general

El modelo relacional de Lyra gestiona las entidades principales de un hotel de cabañas rurales: las cabañas disponibles, los clientes que realizan reservas, los empleados que las gestionan y los servicios extra que se pueden contratar. El núcleo del modelo es la tabla `reserva`, que conecta clientes con cabañas en un rango de fechas concreto.

---

## Tablas

### cabana

Representa cada unidad alojable del hotel. Almacena su precio por noche y si está operativa.

| Columna         | Tipo           | Restricciones                          |
|-----------------|----------------|----------------------------------------|
| id              | SERIAL         | PK, NOT NULL                           |
| nombre          | VARCHAR(100)   | NOT NULL                               |
| descripcion     | TEXT           | —                                      |
| capacidad       | SMALLINT       | NOT NULL, CHECK (capacidad > 0)        |
| precio_noche    | NUMERIC(8,2)   | NOT NULL, CHECK (precio_noche >= 0)    |
| disponible      | BOOLEAN        | NOT NULL, DEFAULT TRUE                 |
| fecha_creacion  | TIMESTAMP      | NOT NULL, DEFAULT NOW()                |

- **PK:** `id`

---

### cliente

Representa a las personas que realizan reservas en el hotel. El DNI y el email son únicos por cliente.

| Columna         | Tipo           | Restricciones                          |
|-----------------|----------------|----------------------------------------|
| id              | SERIAL         | PK, NOT NULL                           |
| nombre          | VARCHAR(100)   | NOT NULL                               |
| apellidos       | VARCHAR(150)   | NOT NULL                               |
| dni             | VARCHAR(20)    | NOT NULL, UNIQUE                       |
| email           | VARCHAR(254)   | NOT NULL, UNIQUE                       |
| telefono        | VARCHAR(20)    | —                                      |
| fecha_registro  | TIMESTAMP      | NOT NULL, DEFAULT NOW()                |

- **PK:** `id`

---

### empleado

Representa al personal del hotel. Un empleado puede estar activo o inactivo (baja, cese, etc.).

| Columna             | Tipo           | Restricciones                          |
|---------------------|----------------|----------------------------------------|
| id                  | SERIAL         | PK, NOT NULL                           |
| nombre              | VARCHAR(100)   | NOT NULL                               |
| apellidos           | VARCHAR(150)   | NOT NULL                               |
| dni                 | VARCHAR(20)    | NOT NULL, UNIQUE                       |
| email               | VARCHAR(254)   | NOT NULL, UNIQUE                       |
| telefono            | VARCHAR(20)    | —                                      |
| cargo               | VARCHAR(100)   | NOT NULL                               |
| fecha_contratacion  | DATE           | NOT NULL                               |
| activo              | BOOLEAN        | NOT NULL, DEFAULT TRUE                 |

- **PK:** `id`

---

### servicio_extra

Catálogo de servicios adicionales que se pueden contratar junto a una reserva (desayuno, limpieza, traslados, etc.).

| Columna      | Tipo           | Restricciones                          |
|--------------|----------------|----------------------------------------|
| id           | SERIAL         | PK, NOT NULL                           |
| nombre       | VARCHAR(100)   | NOT NULL, UNIQUE                       |
| descripcion  | TEXT           | —                                      |
| precio       | NUMERIC(8,2)   | NOT NULL, CHECK (precio >= 0)          |
| disponible   | BOOLEAN        | NOT NULL, DEFAULT TRUE                 |

- **PK:** `id`

---

### reserva

Entidad central del modelo. Relaciona un cliente con una cabaña durante un intervalo de fechas. El precio total lo calcula la capa Java como: `días × precio_noche + suma(cantidad × precio_unitario de cada servicio)`.

| Columna         | Tipo             | Restricciones                                    |
|-----------------|------------------|--------------------------------------------------|
| id              | SERIAL           | PK, NOT NULL                                     |
| id_cliente      | INT              | NOT NULL, FK → cliente(id)                       |
| id_cabana       | INT              | NOT NULL, FK → cabana(id)                        |
| fecha_entrada   | DATE             | NOT NULL                                         |
| fecha_salida    | DATE             | NOT NULL                                         |
| estado          | estado_reserva   | NOT NULL, DEFAULT 'PENDIENTE'                    |
| precio_total    | NUMERIC(10,2)    | — (calculado por Java)                           |
| observaciones   | TEXT             | —                                                |
| fecha_creacion  | TIMESTAMP        | NOT NULL, DEFAULT NOW()                          |

- **PK:** `id`
- **FK:** `id_cliente` → `cliente(id)` — ON DELETE RESTRICT, ON UPDATE CASCADE
- **FK:** `id_cabana` → `cabana(id)` — ON DELETE RESTRICT, ON UPDATE CASCADE
- **CHECK:** `fecha_salida > fecha_entrada`

---

### reserva_servicio

Tabla intermedia entre `reserva` y `servicio_extra`. Registra qué servicios extra se contrataron en cada reserva y en qué cantidad. Guarda un snapshot del precio en el momento de la contratación para mantener el histórico aunque el precio del servicio cambie en el futuro.

| Columna            | Tipo           | Restricciones                                      |
|--------------------|----------------|----------------------------------------------------|
| id_reserva         | INT            | PK (parte), NOT NULL, FK → reserva(id)             |
| id_servicio_extra  | INT            | PK (parte), NOT NULL, FK → servicio_extra(id)      |
| cantidad           | SMALLINT       | NOT NULL, DEFAULT 1, CHECK (cantidad > 0)          |
| precio_unitario    | NUMERIC(8,2)   | NOT NULL, CHECK (precio_unitario >= 0)             |

- **PK compuesta:** (`id_reserva`, `id_servicio_extra`)
- **FK:** `id_reserva` → `reserva(id)` — ON DELETE CASCADE, ON UPDATE CASCADE
- **FK:** `id_servicio_extra` → `servicio_extra(id)` — ON DELETE RESTRICT, ON UPDATE CASCADE

---

### reserva_empleado

Tabla intermedia entre `reserva` y `empleado`. Registra qué empleados intervinieron en cada reserva y en qué función (gestión, check-in, check-out, limpieza). Un mismo empleado puede aparecer varias veces en la misma reserva si desempeñó distintos roles.

| Columna       | Tipo           | Restricciones                                           |
|---------------|----------------|---------------------------------------------------------|
| id_reserva    | INT            | PK (parte), NOT NULL, FK → reserva(id)                 |
| id_empleado   | INT            | PK (parte), NOT NULL, FK → empleado(id)                |
| rol           | VARCHAR(50)    | PK (parte), NOT NULL, DEFAULT 'GESTION'                |
| fecha_accion  | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIMESTAMP                    |
| notas         | TEXT           | —                                                       |

- **PK compuesta:** (`id_reserva`, `id_empleado`, `rol`)
- **FK:** `id_reserva` → `reserva(id)` — ON DELETE CASCADE
- **FK:** `id_empleado` → `empleado(id)` — ON DELETE RESTRICT

---

## Relaciones entre tablas

| Relación                               | Tipo | Descripción                                                                                      |
|----------------------------------------|------|--------------------------------------------------------------------------------------------------|
| `cliente` → `reserva`                  | 1:N  | Un cliente puede tener múltiples reservas, pero cada reserva pertenece a un único cliente.       |
| `cabana` → `reserva`                   | 1:N  | Una cabaña puede aparecer en múltiples reservas, pero cada reserva es para una única cabaña.     |
| `reserva` ↔ `servicio_extra`           | N:M  | Una reserva puede incluir varios servicios extra, y un servicio puede estar en varias reservas. La tabla intermedia `reserva_servicio` resuelve esta relación y añade los atributos `cantidad` y `precio_unitario`. |
| `reserva` ↔ `empleado`                 | N:M  | Una reserva puede ser gestionada por varios empleados, y un empleado puede gestionar múltiples reservas. La tabla intermedia `reserva_empleado` resuelve esta relación y añade `rol`, `fecha_accion` y `notas`. |

---

## Tipo enumerado: estado_reserva

El campo `estado` de la tabla `reserva` utiliza un tipo `ENUM` de PostgreSQL llamado `estado_reserva`. Este tipo restringe los valores posibles a los estados definidos en el ciclo de vida de una reserva:

| Valor        | Significado                                                                 |
|--------------|-----------------------------------------------------------------------------|
| PENDIENTE    | La reserva ha sido creada pero aún no ha sido confirmada por el hotel.      |
| CONFIRMADA   | El hotel ha aceptado la reserva. Queda pendiente de que el cliente llegue.  |
| ACTIVA       | El cliente ha hecho el check-in y está actualmente alojado.                 |
| COMPLETADA   | El cliente ha hecho el check-out y la estancia ha finalizado correctamente. |
| CANCELADA    | La reserva fue anulada, ya sea por el cliente o por el hotel.               |

**Ciclo de vida:**

```
PENDIENTE → CONFIRMADA → ACTIVA → COMPLETADA
                                ↘
                                 CANCELADA
```

El campo `estado` tiene como valor por defecto `PENDIENTE`, de modo que toda reserva recién creada empieza automáticamente en ese estado.
