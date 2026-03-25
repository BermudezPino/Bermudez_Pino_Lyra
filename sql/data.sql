-- =============================================================================
-- Lyra — Sistema de Gestión de Hotel de Cabañas Rurales
-- Script de datos de ejemplo
-- Proyecto Intermodular 1.º DAM — Juan
-- =============================================================================
-- Ejecutar DESPUÉS de schema.sql
-- Idempotente: usa ON CONFLICT DO NOTHING en inserciones de catálogo
-- =============================================================================

BEGIN;

-- -----------------------------------------------------------------------------
-- CABAÑAS
-- -----------------------------------------------------------------------------
INSERT INTO cabana (nombre, descripcion, capacidad, precio_noche, disponible) VALUES
    ('Cabaña del Bosque',   'Cabaña rodeada de pinos con chimenea y vistas al valle', 4, 85.00,  TRUE),
    ('Cabaña del Lago',     'A orillas del lago, terraza privada con embarcadero',    2, 110.00, TRUE),
    ('Cabaña La Cima',      'En lo alto de la montaña, panorámica de 360°',           6, 150.00, TRUE),
    ('Cabaña El Arroyo',    'Junto a un arroyo con zona de barbacoa exterior',         4, 95.00,  TRUE),
    ('Cabaña Los Prados',   'Acceso para personas con movilidad reducida',             3, 75.00,  FALSE)
ON CONFLICT DO NOTHING;

-- -----------------------------------------------------------------------------
-- SERVICIOS EXTRA
-- -----------------------------------------------------------------------------
INSERT INTO servicio_extra (nombre, descripcion, precio, disponible) VALUES
    ('Desayuno',            'Desayuno completo incluido cada mañana',              12.50, TRUE),
    ('Limpieza diaria',     'Servicio de limpieza y cambio de ropa de cama',       20.00, TRUE),
    ('Alquiler de bici',    'Bicicleta de montaña por día',                        15.00, TRUE),
    ('Traslado aeropuerto', 'Recogida y entrega en aeropuerto más cercano',         45.00, TRUE),
    ('Pack romántico',      'Flores, velas y cena para dos en la cabaña',           60.00, TRUE)
ON CONFLICT DO NOTHING;

-- -----------------------------------------------------------------------------
-- CLIENTES
-- -----------------------------------------------------------------------------
INSERT INTO cliente (nombre, apellidos, dni, email, telefono) VALUES
    ('Carlos',    'Martínez López',      '12345678A', 'carlos.martinez@email.com',   '612 345 678'),
    ('María',     'García Fernández',    '23456789B', 'maria.garcia@email.com',      '623 456 789'),
    ('Javier',    'Rodríguez Sánchez',   '34567890C', 'javier.rodriguez@email.com',  '634 567 890'),
    ('Laura',     'López Torres',        '45678901D', 'laura.lopez@email.com',       '645 678 901'),
    ('Antonio',   'Sánchez Moreno',      '56789012E', 'antonio.sanchez@email.com',   '656 789 012'),
    ('Elena',     'Pérez Ruiz',          '67890123F', 'elena.perez@email.com',       '667 890 123'),
    ('Miguel',    'González Jiménez',    '78901234G', 'miguel.gonzalez@email.com',   '678 901 234'),
    ('Sofía',     'Hernández Castro',    '89012345H', 'sofia.hernandez@email.com',   '689 012 345');

-- -----------------------------------------------------------------------------
-- EMPLEADOS
-- -----------------------------------------------------------------------------
INSERT INTO empleado (nombre, apellidos, dni, email, telefono, cargo, fecha_contratacion, activo) VALUES
    ('Rosa',     'Blanco Vega',      '11111111A', 'rosa.blanco@lyra.com',    '611 111 111', 'Recepcionista',  '2023-01-15', TRUE),
    ('Tomás',    'Fuentes Molina',   '22222222B', 'tomas.fuentes@lyra.com',  '622 222 222', 'Mantenimiento',  '2022-06-01', TRUE),
    ('Carmen',   'Iglesias Rubio',   '33333333C', 'carmen.iglesias@lyra.com','633 333 333', 'Limpieza',       '2023-03-10', TRUE),
    ('Pablo',    'Delgado Navarro',  '44444444D', 'pablo.delgado@lyra.com',  '644 444 444', 'Recepcionista',  '2024-09-01', TRUE),
    ('Isabel',   'Serrano Mora',     '55555555E', 'isabel.serrano@lyra.com', '655 555 555', 'Administración', '2021-11-20', FALSE);

-- -----------------------------------------------------------------------------
-- RESERVAS
-- Precios calculados: días × precio_noche + servicios (referencial)
-- -----------------------------------------------------------------------------
INSERT INTO reserva (id_cliente, id_cabana, fecha_entrada, fecha_salida, estado, precio_total, observaciones) VALUES
    -- Completadas (pasadas)
    (1, 1, '2026-01-10', '2026-01-15', 'COMPLETADA',  510.00, 'Primera visita del cliente'),
    (2, 2, '2026-01-20', '2026-01-23', 'COMPLETADA',  378.00, NULL),
    (3, 3, '2026-02-01', '2026-02-07', 'COMPLETADA', 1020.00, 'Grupo familiar, solicitaron cuna'),
    -- Cancelada
    (4, 4, '2026-02-14', '2026-02-16', 'CANCELADA',   NULL,   'Cancelada por el cliente por motivos personales'),
    -- Confirmadas (próximas)
    (5, 1, '2026-03-20', '2026-03-25', 'CONFIRMADA',  555.00, NULL),
    (6, 2, '2026-03-28', '2026-03-30', 'CONFIRMADA',  257.50, 'Aniversario de boda'),
    -- Activa (estancia en curso)
    (7, 5, '2026-03-15', '2026-03-18', 'ACTIVA',      225.00, NULL),
    -- Pendientes (recién creadas)
    (8, 3, '2026-04-05', '2026-04-10', 'PENDIENTE',   NULL,   'Solicita información sobre actividades cercanas'),
    (1, 4, '2026-04-12', '2026-04-14', 'PENDIENTE',   NULL,   NULL),
    (3, 2, '2026-05-01', '2026-05-05', 'PENDIENTE',   NULL,   'Repite cliente, solicitar preferencias previas');

-- -----------------------------------------------------------------------------
-- RESERVA_SERVICIO (servicios contratados por reserva)
-- precio_unitario es snapshot del precio en el momento de contratar
-- -----------------------------------------------------------------------------
INSERT INTO reserva_servicio (id_reserva, id_servicio_extra, cantidad, precio_unitario) VALUES
    -- Reserva 1 (5 noches, cabaña del bosque): desayuno + bici
    (1, 1, 5, 12.50),   -- 5 desayunos
    (1, 3, 2, 15.00),   -- 2 bicis
    -- Reserva 2 (3 noches, cabaña del lago): pack romántico
    (2, 5, 1, 60.00),   -- pack romántico
    (2, 1, 3, 12.50),   -- 3 desayunos
    -- Reserva 3 (6 noches, la cima): desayuno + limpieza
    (3, 1, 6, 12.50),   -- 6 desayunos
    (3, 2, 3, 20.00),   -- limpieza cada 2 días
    -- Reserva 5 (5 noches, confirmada): desayuno
    (5, 1, 5, 12.50),
    -- Reserva 6 (2 noches, aniversario): pack romántico
    (6, 5, 1, 60.00),
    (6, 1, 2, 12.50);

-- -----------------------------------------------------------------------------
-- RESERVA_EMPLEADO (empleados que gestionaron cada reserva)
-- -----------------------------------------------------------------------------
INSERT INTO reserva_empleado (id_reserva, id_empleado, rol, notas) VALUES
    -- Reserva 1 (completada)
    (1, 1, 'GESTION',   'Recepcionista encargada de la gestión inicial'),
    (1, 1, 'CHECK_IN',  NULL),
    (1, 3, 'LIMPIEZA',  'Limpieza de salida'),
    -- Reserva 2 (completada)
    (2, 4, 'GESTION',   NULL),
    (2, 4, 'CHECK_IN',  NULL),
    (2, 4, 'CHECK_OUT', NULL),
    -- Reserva 3 (completada)
    (3, 1, 'GESTION',   NULL),
    (3, 2, 'CHECK_IN',  'Revisión de instalaciones previa al check-in'),
    (3, 3, 'LIMPIEZA',  'Limpieza intermedia y de salida'),
    -- Reserva 7 (activa en curso)
    (7, 4, 'GESTION',   NULL),
    (7, 4, 'CHECK_IN',  NULL);

COMMIT;
