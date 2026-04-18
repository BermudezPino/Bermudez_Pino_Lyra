package com.lyra.database;

import com.lyra.model.EstadoReserva;
import com.lyra.model.Reserva;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservaDAO {

    private Connection getConnection() {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Reserva> findById(int id) {
        String sql = "SELECT id, id_cliente, id_cabana, fecha_entrada, fecha_salida, estado, precio_total, observaciones, fecha_creacion FROM reserva WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<Reserva> findAll() {
        String sql = "SELECT id, id_cliente, id_cabana, fecha_entrada, fecha_salida, estado, precio_total, observaciones, fecha_creacion FROM reserva ORDER BY fecha_creacion DESC";
        List<Reserva> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Reserva> findByCliente(int idCliente) {
        String sql = "SELECT id, id_cliente, id_cabana, fecha_entrada, fecha_salida, estado, precio_total, observaciones, fecha_creacion FROM reserva WHERE id_cliente = ? ORDER BY fecha_creacion DESC";
        List<Reserva> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Reserva> findByEstado(EstadoReserva estado) {
        String sql = "SELECT id, id_cliente, id_cabana, fecha_entrada, fecha_salida, estado, precio_total, observaciones, fecha_creacion FROM reserva WHERE estado = CAST(? AS estado_reserva) ORDER BY fecha_entrada";
        List<Reserva> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setObject(1, estado.name(), Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Integer> findDisponibles(LocalDate entrada, LocalDate salida) {
        String sql = "SELECT c.id FROM cabana c WHERE c.disponible = TRUE AND c.id NOT IN (" +
                     "SELECT r.id_cabana FROM reserva r WHERE r.estado IN ('CONFIRMADA', 'ACTIVA') AND r.fecha_entrada < ? AND r.fecha_salida > ?) ORDER BY c.precio_noche";
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(salida));
            ps.setDate(2, Date.valueOf(entrada));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ids;
    }

    public void save(Reserva r) {
        String sql = "INSERT INTO reserva (id_cliente, id_cabana, fecha_entrada, fecha_salida, estado, precio_total, observaciones) VALUES (?, ?, ?, ?, CAST(? AS estado_reserva), ?, ?) RETURNING id";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, r.getIdCliente());
            ps.setInt(2, r.getIdCabana());
            ps.setDate(3, Date.valueOf(r.getFechaEntrada()));
            ps.setDate(4, Date.valueOf(r.getFechaSalida()));
            ps.setObject(5, r.getEstado().name(), Types.OTHER);
            if (r.getPrecioTotal() != null) ps.setBigDecimal(6, r.getPrecioTotal());
            else ps.setNull(6, Types.NUMERIC);
            ps.setString(7, r.getObservaciones());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) r.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Reserva r) {
        String sql = "UPDATE reserva SET id_cliente = ?, id_cabana = ?, fecha_entrada = ?, fecha_salida = ?, estado = CAST(? AS estado_reserva), precio_total = ?, observaciones = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, r.getIdCliente());
            ps.setInt(2, r.getIdCabana());
            ps.setDate(3, Date.valueOf(r.getFechaEntrada()));
            ps.setDate(4, Date.valueOf(r.getFechaSalida()));
            ps.setObject(5, r.getEstado().name(), Types.OTHER);
            if (r.getPrecioTotal() != null) ps.setBigDecimal(6, r.getPrecioTotal());
            else ps.setNull(6, Types.NUMERIC);
            ps.setString(7, r.getObservaciones());
            ps.setInt(8, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void cambiarEstado(int id, EstadoReserva nuevoEstado) {
        String sql = "UPDATE reserva SET estado = CAST(? AS estado_reserva) WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setObject(1, nuevoEstado.name(), Types.OTHER);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePrecioTotal(int id, BigDecimal precio) {
        String sql = "UPDATE reserva SET precio_total = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setBigDecimal(1, precio);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM reserva WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Reserva mapRow(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();
        r.setId(rs.getInt("id"));
        r.setIdCliente(rs.getInt("id_cliente"));
        r.setIdCabana(rs.getInt("id_cabana"));
        java.sql.Date fechaEntrada = rs.getDate("fecha_entrada");
        r.setFechaEntrada(fechaEntrada != null ? fechaEntrada.toLocalDate() : null);
        java.sql.Date fechaSalida = rs.getDate("fecha_salida");
        r.setFechaSalida(fechaSalida != null ? fechaSalida.toLocalDate() : null);
        r.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
        BigDecimal precio = rs.getBigDecimal("precio_total");
        r.setPrecioTotal(rs.wasNull() ? null : precio);
        r.setObservaciones(rs.getString("observaciones"));
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) r.setFechaCreacion(ts.toLocalDateTime());
        return r;
    }
}
