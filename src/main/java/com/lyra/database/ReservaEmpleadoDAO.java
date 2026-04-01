package com.lyra.database;

import com.lyra.model.ReservaEmpleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaEmpleadoDAO {

    private Connection getConnection() {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ReservaEmpleado> findByReserva(int idReserva) {
        String sql = "SELECT id_reserva, id_empleado, rol, fecha_accion, notas FROM reserva_empleado WHERE id_reserva = ?";
        List<ReservaEmpleado> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<ReservaEmpleado> findByEmpleado(int idEmpleado) {
        String sql = "SELECT id_reserva, id_empleado, rol, fecha_accion, notas FROM reserva_empleado WHERE id_empleado = ?";
        List<ReservaEmpleado> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void save(ReservaEmpleado re) {
        String sql = "INSERT INTO reserva_empleado (id_reserva, id_empleado, rol, notas) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, re.getIdReserva());
            ps.setInt(2, re.getIdEmpleado());
            ps.setString(3, re.getRol());
            ps.setString(4, re.getNotas());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int idReserva, int idEmpleado, String rol) {
        String sql = "DELETE FROM reserva_empleado WHERE id_reserva = ? AND id_empleado = ? AND rol = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, idEmpleado);
            ps.setString(3, rol);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ReservaEmpleado mapRow(ResultSet rs) throws SQLException {
        ReservaEmpleado re = new ReservaEmpleado();
        re.setIdReserva(rs.getInt("id_reserva"));
        re.setIdEmpleado(rs.getInt("id_empleado"));
        re.setRol(rs.getString("rol"));
        Timestamp ts = rs.getTimestamp("fecha_accion");
        if (ts != null) re.setFechaAccion(ts.toLocalDateTime());
        re.setNotas(rs.getString("notas"));
        return re;
    }
}
