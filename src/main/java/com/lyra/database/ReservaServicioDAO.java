package com.lyra.database;

import com.lyra.model.ReservaServicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaServicioDAO {

    private Connection getConnection() {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ReservaServicio> findByReserva(int idReserva) {
        String sql = "SELECT id_reserva, id_servicio_extra, cantidad, precio_unitario FROM reserva_servicio WHERE id_reserva = ?";
        List<ReservaServicio> list = new ArrayList<>();
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

    public void save(ReservaServicio rs) {
        String sql = "INSERT INTO reserva_servicio (id_reserva, id_servicio_extra, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, rs.getIdReserva());
            ps.setInt(2, rs.getIdServicioExtra());
            ps.setInt(3, rs.getCantidad());
            ps.setBigDecimal(4, rs.getPrecioUnitario());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int idReserva, int idServicioExtra) {
        String sql = "DELETE FROM reserva_servicio WHERE id_reserva = ? AND id_servicio_extra = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, idServicioExtra);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByReserva(int idReserva) {
        String sql = "DELETE FROM reserva_servicio WHERE id_reserva = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ReservaServicio mapRow(ResultSet rs) throws SQLException {
        ReservaServicio obj = new ReservaServicio();
        obj.setIdReserva(rs.getInt("id_reserva"));
        obj.setIdServicioExtra(rs.getInt("id_servicio_extra"));
        obj.setCantidad(rs.getInt("cantidad"));
        obj.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        return obj;
    }
}
