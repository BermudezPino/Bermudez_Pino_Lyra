package com.lyra.database;

import com.lyra.model.Cabana;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CabanaDAO {

    private Connection getConnection() {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Cabana> findById(int id) {
        String sql = "SELECT id, nombre, descripcion, capacidad, precio_noche, disponible, fecha_creacion FROM cabana WHERE id = ?";
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

    public List<Cabana> findAll() {
        String sql = "SELECT id, nombre, descripcion, capacidad, precio_noche, disponible, fecha_creacion FROM cabana ORDER BY id";
        List<Cabana> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void save(Cabana c) {
        String sql = "INSERT INTO cabana (nombre, descripcion, capacidad, precio_noche, disponible) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt(3, c.getCapacidad());
            ps.setBigDecimal(4, c.getPrecioNoche());
            ps.setBoolean(5, c.isDisponible());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Cabana c) {
        String sql = "UPDATE cabana SET nombre = ?, descripcion = ?, capacidad = ?, precio_noche = ?, disponible = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt(3, c.getCapacidad());
            ps.setBigDecimal(4, c.getPrecioNoche());
            ps.setBoolean(5, c.isDisponible());
            ps.setInt(6, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM cabana WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Cabana mapRow(ResultSet rs) throws SQLException {
        Cabana c = new Cabana();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setCapacidad(rs.getInt("capacidad"));
        c.setPrecioNoche(rs.getBigDecimal("precio_noche"));
        c.setDisponible(rs.getBoolean("disponible"));
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) c.setFechaCreacion(ts.toLocalDateTime());
        return c;
    }
}
