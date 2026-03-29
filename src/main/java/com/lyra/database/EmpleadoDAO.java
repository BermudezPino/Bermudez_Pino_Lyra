package com.lyra.database;

import com.lyra.model.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpleadoDAO {

    private Connection getConnection() {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Empleado> findById(int id) {
        String sql = "SELECT id, nombre, apellidos, dni, email, telefono, cargo, fecha_contratacion, activo FROM empleado WHERE id = ?";
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

    public List<Empleado> findAll() {
        String sql = "SELECT id, nombre, apellidos, dni, email, telefono, cargo, fecha_contratacion, activo FROM empleado ORDER BY apellidos, nombre";
        List<Empleado> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void save(Empleado e) {
        String sql = "INSERT INTO empleado (nombre, apellidos, dni, email, telefono, cargo, fecha_contratacion, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellidos());
            ps.setString(3, e.getDni());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getTelefono());
            ps.setString(6, e.getCargo());
            ps.setDate(7, e.getFechaContratacion() != null ? Date.valueOf(e.getFechaContratacion()) : null);
            ps.setBoolean(8, e.isActivo());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void update(Empleado e) {
        String sql = "UPDATE empleado SET nombre = ?, apellidos = ?, dni = ?, email = ?, telefono = ?, cargo = ?, fecha_contratacion = ?, activo = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellidos());
            ps.setString(3, e.getDni());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getTelefono());
            ps.setString(6, e.getCargo());
            ps.setDate(7, e.getFechaContratacion() != null ? Date.valueOf(e.getFechaContratacion()) : null);
            ps.setBoolean(8, e.isActivo());
            ps.setInt(9, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM empleado WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deactivate(int id) {
        String sql = "UPDATE empleado SET activo = FALSE WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Empleado mapRow(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setApellidos(rs.getString("apellidos"));
        e.setDni(rs.getString("dni"));
        e.setEmail(rs.getString("email"));
        e.setTelefono(rs.getString("telefono"));
        e.setCargo(rs.getString("cargo"));
        Date fecha = rs.getDate("fecha_contratacion");
        if (fecha != null) e.setFechaContratacion(fecha.toLocalDate());
        e.setActivo(rs.getBoolean("activo"));
        return e;
    }
}
