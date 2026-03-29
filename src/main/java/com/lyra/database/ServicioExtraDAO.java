package com.lyra.database;

import com.lyra.model.ServicioExtra;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicioExtraDAO {

    private Connection getConnection() {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ServicioExtra> findById(int id) {
        String sql = "SELECT id, nombre, descripcion, precio, disponible FROM servicio_extra WHERE id = ?";
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

    public List<ServicioExtra> findAll() {
        String sql = "SELECT id, nombre, descripcion, precio, disponible FROM servicio_extra ORDER BY nombre";
        List<ServicioExtra> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void save(ServicioExtra s) {
        String sql = "INSERT INTO servicio_extra (nombre, descripcion, precio, disponible) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setBigDecimal(3, s.getPrecio());
            ps.setBoolean(4, s.isDisponible());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(ServicioExtra s) {
        String sql = "UPDATE servicio_extra SET nombre = ?, descripcion = ?, precio = ?, disponible = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setBigDecimal(3, s.getPrecio());
            ps.setBoolean(4, s.isDisponible());
            ps.setInt(5, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM servicio_extra WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deactivate(int id) {
        String sql = "UPDATE servicio_extra SET disponible = FALSE WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ServicioExtra mapRow(ResultSet rs) throws SQLException {
        ServicioExtra s = new ServicioExtra();
        s.setId(rs.getInt("id"));
        s.setNombre(rs.getString("nombre"));
        s.setDescripcion(rs.getString("descripcion"));
        s.setPrecio(rs.getBigDecimal("precio"));
        s.setDisponible(rs.getBoolean("disponible"));
        return s;
    }
}
