-- =============================================================================
-- Lyra — Sistema de Gestión de Hotel de Cabañas Rurales
-- Consultas SQL para DAOs Java (JDBC PreparedStatement)
-- Proyecto Intermodular 1.º DAM — Juan
-- PostgreSQL 15 (Supabase)
-- =============================================================================
-- CONVENCIÓN:
--   - Todos los valores dinámicos se marcan con ? (estilo JDBC PreparedStatement)
--   - SQL en MAYÚSCULAS
--   - Cada query lleva un comentario con su propósito y el DAO que la usa
-- =============================================================================


-- =============================================================================
-- SECCIÓN 1: CABANA
-- =============================================================================

-- Obtener todas las cabañas (CabanaDAO.findAll)
SELECT id, nombre, descripcion, capacidad, precio_noche, disponible, fecha_creacion
FROM cabana
ORDER BY id;

-- Obtener una cabaña por su id (CabanaDAO.findById)
SELECT id, nombre, descripcion, capacidad, precio_noche, disponible, fecha_creacion
FROM cabana
WHERE id = ?;

-- Obtener cabañas disponibles en un rango de fechas dado
-- Una cabaña está disponible si:
--   1. Su flag disponible = TRUE
--   2. No tiene ninguna reserva CONFIRMADA o ACTIVA que solape las fechas pedidas
-- Solapamiento: la reserva existente empieza antes de que salgas y termina después de que entres
-- (CabanaDAO.findDisponibles)
SELECT c.id, c.nombre, c.descripcion, c.capacidad, c.precio_noche, c.disponible, c.fecha_creacion
FROM cabana c
WHERE c.disponible = TRUE
  AND c.id NOT IN (
      SELECT r.id_cabana
      FROM reserva r
      WHERE r.estado IN ('CONFIRMADA', 'ACTIVA')
        AND r.fecha_entrada < ?   -- fecha_salida buscada
        AND r.fecha_salida  > ?   -- fecha_entrada buscada
  )
ORDER BY c.precio_noche;

-- Insertar una nueva cabaña (CabanaDAO.insert)
INSERT INTO cabana (nombre, descripcion, capacidad, precio_noche, disponible)
VALUES (?, ?, ?, ?, ?);

-- Actualizar los datos de una cabaña (CabanaDAO.update)
UPDATE cabana
SET nombre       = ?,
    descripcion  = ?,
    capacidad    = ?,
    precio_noche = ?,
    disponible   = ?
WHERE id = ?;

-- Eliminar una cabaña por id (CabanaDAO.delete)
-- Nota: fallará si existe alguna reserva asociada (ON DELETE RESTRICT)
DELETE FROM cabana
WHERE id = ?;


-- =============================================================================
-- SECCIÓN 2: CLIENTE
-- =============================================================================

-- Obtener todos los clientes (ClienteDAO.findAll)
SELECT id, nombre, apellidos, dni, email, telefono, fecha_registro
FROM cliente
ORDER BY apellidos, nombre;

-- Obtener un cliente por su id (ClienteDAO.findById)
SELECT id, nombre, apellidos, dni, email, telefono, fecha_registro
FROM cliente
WHERE id = ?;

-- Obtener un cliente por su email (ClienteDAO.findByEmail)
-- Útil para comprobar duplicados o para login
SELECT id, nombre, apellidos, dni, email, telefono, fecha_registro
FROM cliente
WHERE email = ?;

-- Buscar cliente por DNI (para check-in en recepción)
SELECT * FROM cliente WHERE dni = ?;

-- Insertar un nuevo cliente (ClienteDAO.insert)
INSERT INTO cliente (nombre, apellidos, dni, email, telefono)
VALUES (?, ?, ?, ?, ?);

-- Actualizar los datos de un cliente (ClienteDAO.update)
UPDATE cliente
SET nombre    = ?,
    apellidos = ?,
    dni       = ?,
    email     = ?,
    telefono  = ?
WHERE id = ?;


-- =============================================================================
-- SECCIÓN 3: EMPLEADO
-- =============================================================================

-- Obtener todos los empleados activos (EmpleadoDAO.findAll)
SELECT id, nombre, apellidos, dni, email, telefono, cargo, fecha_contratacion, activo
FROM empleado
ORDER BY apellidos, nombre;

-- Obtener un empleado por su id (EmpleadoDAO.findById)
SELECT id, nombre, apellidos, dni, email, telefono, cargo, fecha_contratacion, activo
FROM empleado
WHERE id = ?;

-- Insertar un nuevo empleado (EmpleadoDAO.insert)
INSERT INTO empleado (nombre, apellidos, dni, email, telefono, cargo, fecha_contratacion, activo)
VALUES (?, ?, ?, ?, ?, ?, ?, ?);

-- Actualizar los datos de un empleado (EmpleadoDAO.update)
UPDATE empleado
SET nombre             = ?,
    apellidos          = ?,
    dni                = ?,
    email              = ?,
    telefono           = ?,
    cargo              = ?,
    fecha_contratacion = ?,
    activo             = ?
WHERE id = ?;

-- Baja lógica de un empleado (EmpleadoDAO.deactivate)
UPDATE empleado SET activo = FALSE WHERE id = ?;


-- =============================================================================
-- SECCIÓN 4: SERVICIO_EXTRA
-- =============================================================================

-- Obtener todos los servicios extra (ServicioExtraDAO.findAll)
SELECT id, nombre, descripcion, precio, disponible
FROM servicio_extra
ORDER BY nombre;

-- Obtener un servicio extra por su id (ServicioExtraDAO.findById)
SELECT id, nombre, descripcion, precio, disponible
FROM servicio_extra
WHERE id = ?;

-- Insertar un nuevo servicio extra (ServicioExtraDAO.insert)
INSERT INTO servicio_extra (nombre, descripcion, precio, disponible)
VALUES (?, ?, ?, ?);

-- Actualizar un servicio extra (ServicioExtraDAO.update)
UPDATE servicio_extra
SET nombre      = ?,
    descripcion = ?,
    precio      = ?,
    disponible  = ?
WHERE id = ?;

-- Desactivar un servicio extra (ServicioExtraDAO.deactivate)
UPDATE servicio_extra SET disponible = FALSE WHERE id = ?;


-- =============================================================================
-- SECCIÓN 5: RESERVA
-- =============================================================================

-- Obtener todas las reservas con datos de cabaña y cliente (ReservaDAO.findAll)
SELECT r.id,
       r.id_cliente,
       cl.nombre     AS cliente_nombre,
       cl.apellidos  AS cliente_apellidos,
       r.id_cabana,
       ca.nombre     AS cabana_nombre,
       r.fecha_entrada,
       r.fecha_salida,
       r.estado,
       r.precio_total,
       r.observaciones,
       r.fecha_creacion
FROM reserva r
JOIN cliente cl ON cl.id = r.id_cliente
JOIN cabana  ca ON ca.id = r.id_cabana
ORDER BY r.fecha_creacion DESC;

-- Obtener una reserva por id con todos sus servicios extra (ReservaDAO.findById)
SELECT r.id,
       r.id_cliente,
       cl.nombre     AS cliente_nombre,
       cl.apellidos  AS cliente_apellidos,
       r.id_cabana,
       ca.nombre     AS cabana_nombre,
       ca.precio_noche,
       r.fecha_entrada,
       r.fecha_salida,
       r.estado,
       r.precio_total,
       r.observaciones,
       r.fecha_creacion
FROM reserva r
JOIN cliente cl ON cl.id = r.id_cliente
JOIN cabana  ca ON ca.id = r.id_cabana
WHERE r.id = ?;

-- Obtener reservas de un cliente específico (ReservaDAO.findByCliente)
SELECT r.id,
       r.id_cabana,
       ca.nombre    AS cabana_nombre,
       r.fecha_entrada,
       r.fecha_salida,
       r.estado,
       r.precio_total,
       r.fecha_creacion
FROM reserva r
JOIN cabana ca ON ca.id = r.id_cabana
WHERE r.id_cliente = ?
ORDER BY r.fecha_creacion DESC;

-- Obtener reservas filtradas por estado (ReservaDAO.findByEstado)
-- El estado se pasa como String y se castea a ENUM en PostgreSQL
SELECT r.id,
       r.id_cliente,
       cl.nombre     AS cliente_nombre,
       cl.apellidos  AS cliente_apellidos,
       r.id_cabana,
       ca.nombre     AS cabana_nombre,
       r.fecha_entrada,
       r.fecha_salida,
       r.estado,
       r.precio_total,
       r.fecha_creacion
FROM reserva r
JOIN cliente cl ON cl.id = r.id_cliente
JOIN cabana  ca ON ca.id = r.id_cabana
WHERE r.estado = CAST(? AS estado_reserva)
ORDER BY r.fecha_entrada;

-- Obtener estado actual de una reserva (para validar transiciones en el Service)
SELECT estado FROM reserva WHERE id = ?;

-- Insertar una nueva reserva (ReservaDAO.insert)
-- precio_total puede ser NULL al crear; se actualiza tras añadir servicios
INSERT INTO reserva (id_cliente, id_cabana, fecha_entrada, fecha_salida, estado, precio_total, observaciones)
VALUES (?, ?, ?, ?, CAST(? AS estado_reserva), ?, ?);

-- Actualizar el estado de una reserva (ReservaDAO.updateEstado)
UPDATE reserva
SET estado = CAST(? AS estado_reserva)
WHERE id = ?;

-- Actualizar el precio total de una reserva (ReservaDAO.updatePrecioTotal)
-- Se llama desde ReservaService tras calcular días × precio_noche + servicios
UPDATE reserva
SET precio_total = ?
WHERE id = ?;


-- =============================================================================
-- SECCIÓN 6: RESERVA_SERVICIO
-- =============================================================================

-- Añadir un servicio extra a una reserva (ReservaServicioDAO.insert)
-- precio_unitario es un snapshot del precio actual del servicio
INSERT INTO reserva_servicio (id_reserva, id_servicio_extra, cantidad, precio_unitario)
VALUES (?, ?, ?, ?);

-- Eliminar un servicio extra de una reserva (ReservaServicioDAO.delete)
DELETE FROM reserva_servicio
WHERE id_reserva = ?
  AND id_servicio_extra = ?;

-- Obtener todos los servicios extra de una reserva (ReservaServicioDAO.findByReserva)
SELECT rs.id_reserva,
       rs.id_servicio_extra,
       se.nombre          AS servicio_nombre,
       se.descripcion     AS servicio_descripcion,
       rs.cantidad,
       rs.precio_unitario,
       (rs.cantidad * rs.precio_unitario) AS subtotal
FROM reserva_servicio rs
JOIN servicio_extra se ON se.id = rs.id_servicio_extra
WHERE rs.id_reserva = ?
ORDER BY se.nombre;


-- =============================================================================
-- SECCIÓN 7: RESERVA_EMPLEADO
-- =============================================================================

-- Asignar un empleado a una reserva con un rol (ReservaEmpleadoDAO.insert)
INSERT INTO reserva_empleado (id_reserva, id_empleado, rol, notas)
VALUES (?, ?, ?, ?);

-- Desasignar empleado de una reserva
DELETE FROM reserva_empleado WHERE id_reserva = ? AND id_empleado = ? AND rol = ?;

-- Obtener todas las reservas gestionadas por un empleado (ReservaEmpleadoDAO.findByEmpleado)
SELECT re.id_reserva,
       re.id_empleado,
       re.rol,
       re.fecha_accion,
       re.notas,
       r.fecha_entrada,
       r.fecha_salida,
       r.estado,
       ca.nombre AS cabana_nombre,
       cl.nombre     AS cliente_nombre,
       cl.apellidos  AS cliente_apellidos
FROM reserva_empleado re
JOIN reserva  r  ON r.id  = re.id_reserva
JOIN cabana   ca ON ca.id = r.id_cabana
JOIN cliente  cl ON cl.id = r.id_cliente
WHERE re.id_empleado = ?
ORDER BY re.fecha_accion DESC;

-- Obtener empleados asignados a una reserva concreta
SELECT re.id_reserva,
       re.id_empleado,
       re.rol,
       re.fecha_accion,
       re.notas,
       e.nombre,
       e.apellidos,
       e.cargo
FROM reserva_empleado re
JOIN empleado e ON e.id = re.id_empleado
WHERE re.id_reserva = ?;


-- =============================================================================
-- SECCIÓN 8: CONSULTAS DE NEGOCIO
-- =============================================================================

-- Calcular el precio total de una reserva
-- Fórmula: (días de estancia × precio_noche de la cabaña) + SUM(cantidad × precio_unitario de servicios)
-- Las columnas fecha_entrada y fecha_salida son tipo DATE: la resta devuelve INTEGER de días directamente
-- Usado en ReservaService.calcularPrecioTotal(idReserva)
SELECT
    ((r.fecha_salida - r.fecha_entrada) * ca.precio_noche)
    +
    COALESCE(
        (SELECT SUM(rs.cantidad * rs.precio_unitario)
         FROM reserva_servicio rs
         WHERE rs.id_reserva = r.id),
        0
    ) AS precio_total_calculado
FROM reserva r
JOIN cabana ca ON ca.id = r.id_cabana
WHERE r.id = ?;

-- Resumen de reservas agrupadas por estado
-- Útil para el panel de control del menú principal
SELECT estado,
       COUNT(*)                      AS total_reservas,
       SUM(precio_total)             AS ingresos_totales
FROM reserva
GROUP BY estado
ORDER BY estado;

-- Carga de trabajo por empleado: número de reservas gestionadas por cada empleado
-- Filtrable por rango de fechas de acción
-- Usado en informes de gestión (ReservaEmpleadoDAO.findCargaTrabajo)
SELECT e.id,
       e.nombre,
       e.apellidos,
       e.cargo,
       COUNT(DISTINCT re.id_reserva) AS reservas_distintas
FROM empleado e
LEFT JOIN reserva_empleado re ON re.id_empleado = e.id
WHERE e.activo = TRUE
GROUP BY e.id, e.nombre, e.apellidos, e.cargo
ORDER BY reservas_distintas DESC;

-- Reservas activas hoy: cabañas que tienen huéspedes en este momento
-- Usado para el cuadro de situación diario
SELECT r.id,
       ca.nombre     AS cabana_nombre,
       cl.nombre     AS cliente_nombre,
       cl.apellidos  AS cliente_apellidos,
       cl.telefono,
       r.fecha_entrada,
       r.fecha_salida,
       r.precio_total
FROM reserva r
JOIN cabana  ca ON ca.id = r.id_cabana
JOIN cliente cl ON cl.id = r.id_cliente
WHERE r.estado = 'ACTIVA'
  AND CURRENT_DATE BETWEEN r.fecha_entrada AND r.fecha_salida
ORDER BY r.fecha_salida;

-- Reservas confirmadas con check-in en los próximos 7 días
SELECT r.*, cl.nombre AS cliente_nombre, cl.apellidos AS cliente_apellidos,
       ca.nombre AS cabana_nombre
FROM reserva r
JOIN cliente cl ON cl.id = r.id_cliente
JOIN cabana ca ON ca.id = r.id_cabana
WHERE r.estado = 'CONFIRMADA'::estado_reserva
AND r.fecha_entrada BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days'
ORDER BY r.fecha_entrada ASC;
