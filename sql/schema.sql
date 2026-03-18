-- =============================================================================
-- Lyra — Sistema de Gestión de Hotel de Cabañas Rurales
-- Schema PostgreSQL completo
-- Proyecto Intermodular 1.º DAM — Juan
-- =============================================================================

-- -----------------------------------------------------------------------------
-- ENUM: estado de reserva
-- Ciclo de vida: PENDIENTE → CONFIRMADA → ACTIVA → COMPLETADA / CANCELADA
-- -----------------------------------------------------------------------------
DO $$ BEGIN
    CREATE TYPE estado_reserva AS ENUM (
        'PENDIENTE',
        'CONFIRMADA',
        'ACTIVA',
        'COMPLETADA',
        'CANCELADA'
    );
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

-- -----------------------------------------------------------------------------
-- TABLA: cabana
-- Unidad alojable del hotel. Tiene un precio por noche y flag de disponibilidad.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cabana (
    id               SERIAL        PRIMARY KEY,
    nombre           VARCHAR(100)  NOT NULL,
    descripcion      TEXT,
    capacidad        SMALLINT      NOT NULL CHECK (capacidad > 0),
    precio_noche     NUMERIC(8,2)  NOT NULL CHECK (precio_noche >= 0),
    disponible       BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion   TIMESTAMP     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  cabana                IS 'Unidades alojables del hotel';
COMMENT ON COLUMN cabana.capacidad      IS 'Número máximo de ocupantes';
COMMENT ON COLUMN cabana.precio_noche   IS 'Precio en euros por noche';
COMMENT ON COLUMN cabana.disponible     IS 'FALSE cuando está en mantenimiento o fuera de servicio';

-- -----------------------------------------------------------------------------
-- TABLA: cliente
-- Persona que realiza reservas. DNI único.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cliente (
    id               SERIAL        PRIMARY KEY,
    nombre           VARCHAR(100)  NOT NULL,
    apellidos        VARCHAR(150)  NOT NULL,
    dni              VARCHAR(20)   NOT NULL UNIQUE,
    email            VARCHAR(254)  NOT NULL UNIQUE,
    telefono         VARCHAR(20),
    fecha_registro   TIMESTAMP     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  cliente          IS 'Clientes que realizan reservas';
COMMENT ON COLUMN cliente.dni      IS 'Documento Nacional de Identidad, único por cliente';
COMMENT ON COLUMN cliente.email    IS 'Correo electrónico, usado como contacto principal';

-- -----------------------------------------------------------------------------
-- TABLA: empleado
-- Personal del hotel. DNI único.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS empleado (
    id                  SERIAL        PRIMARY KEY,
    nombre              VARCHAR(100)  NOT NULL,
    apellidos           VARCHAR(150)  NOT NULL,
    dni                 VARCHAR(20)   NOT NULL UNIQUE,
    email               VARCHAR(254)  NOT NULL UNIQUE,
    telefono            VARCHAR(20),
    cargo               VARCHAR(100)  NOT NULL,
    fecha_contratacion  DATE          NOT NULL,
    activo              BOOLEAN       NOT NULL DEFAULT TRUE
);

COMMENT ON TABLE  empleado                   IS 'Personal del hotel';
COMMENT ON COLUMN empleado.cargo             IS 'Puesto o rol del empleado (Recepcionista, Mantenimiento, etc.)';
COMMENT ON COLUMN empleado.fecha_contratacion IS 'Fecha de incorporación al hotel';
COMMENT ON COLUMN empleado.activo            IS 'FALSE cuando el empleado ya no trabaja en el hotel';

-- -----------------------------------------------------------------------------
-- TABLA: servicio_extra
-- Servicios adicionales que se pueden contratar junto a una reserva.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS servicio_extra (
    id           SERIAL        PRIMARY KEY,
    nombre       VARCHAR(100)  NOT NULL UNIQUE,
    descripcion  TEXT,
    precio       NUMERIC(8,2)  NOT NULL CHECK (precio >= 0),
    disponible   BOOLEAN       NOT NULL DEFAULT TRUE
);

COMMENT ON TABLE  servicio_extra           IS 'Servicios adicionales contratables (desayuno, limpieza, etc.)';
COMMENT ON COLUMN servicio_extra.precio    IS 'Precio unitario del servicio en euros';
COMMENT ON COLUMN servicio_extra.disponible IS 'FALSE cuando el servicio está temporalmente desactivado';

-- -----------------------------------------------------------------------------
-- TABLA: reserva
-- Relación entre un cliente y una cabaña en un rango de fechas.
-- precio_total se calcula: días × precio_noche + suma(cantidad × precio_unitario)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reserva (
    id               SERIAL          PRIMARY KEY,
    id_cliente       INT             NOT NULL,
    id_cabana        INT             NOT NULL,
    fecha_entrada    DATE            NOT NULL,
    fecha_salida     DATE            NOT NULL,
    estado           estado_reserva  NOT NULL DEFAULT 'PENDIENTE',
    precio_total     NUMERIC(10,2),                          -- calculado por la capa Java
    observaciones    TEXT,
    fecha_creacion   TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_reserva_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_reserva_cabana
        FOREIGN KEY (id_cabana) REFERENCES cabana(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_fechas
        CHECK (fecha_salida > fecha_entrada)
);

COMMENT ON TABLE  reserva              IS 'Reservas de cabañas por parte de los clientes';
COMMENT ON COLUMN reserva.precio_total IS 'Precio total calculado por Java: días×precio_noche + servicios';
COMMENT ON COLUMN reserva.estado       IS 'Ciclo de vida: PENDIENTE→CONFIRMADA→ACTIVA→COMPLETADA/CANCELADA';

-- Índices para consultas frecuentes
CREATE INDEX IF NOT EXISTS idx_reserva_cliente  ON reserva(id_cliente);
CREATE INDEX IF NOT EXISTS idx_reserva_cabana   ON reserva(id_cabana);
CREATE INDEX IF NOT EXISTS idx_reserva_estado   ON reserva(estado);
CREATE INDEX IF NOT EXISTS idx_reserva_fechas   ON reserva(fecha_entrada, fecha_salida);

-- -----------------------------------------------------------------------------
-- TABLA: reserva_servicio
-- Tabla intermedia entre Reserva y ServicioExtra (N:M con atributos propios).
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reserva_servicio (
    id_reserva        INT            NOT NULL,
    id_servicio_extra INT            NOT NULL,
    cantidad          SMALLINT       NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    precio_unitario   NUMERIC(8,2)   NOT NULL CHECK (precio_unitario >= 0),  -- snapshot del precio en el momento de la reserva

    PRIMARY KEY (id_reserva, id_servicio_extra),

    CONSTRAINT fk_rs_reserva
        FOREIGN KEY (id_reserva) REFERENCES reserva(id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT fk_rs_servicio
        FOREIGN KEY (id_servicio_extra) REFERENCES servicio_extra(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE  reserva_servicio                 IS 'Servicios extra contratados en cada reserva';
COMMENT ON COLUMN reserva_servicio.cantidad        IS 'Unidades contratadas del servicio';
COMMENT ON COLUMN reserva_servicio.precio_unitario IS 'Snapshot del precio en el momento de contratar (histórico)';

-- -----------------------------------------------------------------------------
-- TABLA: reserva_empleado
-- Tabla intermedia entre Reserva y Empleado (N:M con atributos propios).
-- Permite registrar qué empleados gestionaron cada reserva y en qué rol.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reserva_empleado (
    id_reserva   INT          NOT NULL,
    id_empleado  INT          NOT NULL,
    rol          VARCHAR(50)  NOT NULL DEFAULT 'GESTION',  -- ej: GESTION, CHECK_IN, CHECK_OUT, LIMPIEZA
    fecha_accion TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notas        TEXT,

    PRIMARY KEY (id_reserva, id_empleado, rol),

    CONSTRAINT fk_re_reserva
        FOREIGN KEY (id_reserva)  REFERENCES reserva(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_re_empleado
        FOREIGN KEY (id_empleado) REFERENCES empleado(id)
        ON DELETE RESTRICT
);

COMMENT ON TABLE  reserva_empleado              IS 'Seguimiento de empleados que gestionaron cada reserva';
COMMENT ON COLUMN reserva_empleado.rol          IS 'Función del empleado: GESTION, CHECK_IN, CHECK_OUT, LIMPIEZA';
COMMENT ON COLUMN reserva_empleado.fecha_accion IS 'Momento en que el empleado realizó la acción';
COMMENT ON COLUMN reserva_empleado.notas        IS 'Observaciones opcionales sobre la gestión';

CREATE INDEX IF NOT EXISTS idx_reserva_empleado_empleado ON reserva_empleado(id_empleado);
CREATE INDEX IF NOT EXISTS idx_reserva_empleado_reserva  ON reserva_empleado(id_reserva);

-- -----------------------------------------------------------------------------
-- DATOS DE EJEMPLO (cabañas y servicios) — útiles para pruebas JDBC
-- Idempotentes gracias a ON CONFLICT DO NOTHING
-- -----------------------------------------------------------------------------

INSERT INTO cabana (nombre, descripcion, capacidad, precio_noche, disponible) VALUES
    ('Cabaña del Bosque',   'Cabaña rodeada de pinos con chimenea y vistas al valle', 4, 85.00,  TRUE),
    ('Cabaña del Lago',     'A orillas del lago, terraza privada con embarcadero',    2, 110.00, TRUE),
    ('Cabaña La Cima',      'En lo alto de la montaña, panorámica de 360°',           6, 150.00, TRUE),
    ('Cabaña El Arroyo',    'Junto a un arroyo con zona de barbacoa exterior',         4, 95.00,  TRUE),
    ('Cabaña Los Prados',   'Acceso para personas con movilidad reducida',             3, 75.00,  TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO servicio_extra (nombre, descripcion, precio, disponible) VALUES
    ('Desayuno',            'Desayuno completo incluido cada mañana',             12.50, TRUE),
    ('Limpieza diaria',     'Servicio de limpieza y cambio de ropa de cama',      20.00, TRUE),
    ('Alquiler de bici',    'Bicicleta de montaña por día',                        15.00, TRUE),
    ('Traslado aeropuerto', 'Recogida y entrega en aeropuerto más cercano',        45.00, TRUE),
    ('Pack romántico',      'Flores, velas y cena para dos en la cabaña',          60.00, TRUE)
ON CONFLICT DO NOTHING;
