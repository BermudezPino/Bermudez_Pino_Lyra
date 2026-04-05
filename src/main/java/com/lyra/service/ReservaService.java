package com.lyra.service;

import com.lyra.database.CabanaDAO;
import com.lyra.database.ReservaDAO;
import com.lyra.database.ReservaServicioDAO;
import com.lyra.model.Cabana;
import com.lyra.model.EstadoReserva;
import com.lyra.model.Reserva;
import com.lyra.model.ReservaServicio;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReservaService {

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final ReservaServicioDAO reservaServicioDAO = new ReservaServicioDAO();
    private final CabanaDAO cabanaDAO = new CabanaDAO();

    public void crearReserva(Reserva reserva, List<ReservaServicio> servicios) {
        reservaDAO.save(reserva);
        for (ReservaServicio rs : servicios) {
            rs.setIdReserva(reserva.getId());
            reservaServicioDAO.save(rs);
        }
        BigDecimal total = calcularPrecioTotal(reserva, servicios);
        reservaDAO.updatePrecioTotal(reserva.getId(), total);
        reserva.setPrecioTotal(total);
    }

    public BigDecimal calcularPrecioTotal(Reserva reserva, List<ReservaServicio> servicios) {
        Cabana cabana = cabanaDAO.findById(reserva.getIdCabana())
                .orElseThrow(() -> new IllegalArgumentException("Cabaña no encontrada"));
        long dias = ChronoUnit.DAYS.between(reserva.getFechaEntrada(), reserva.getFechaSalida());
        BigDecimal precioAlojamiento = cabana.getPrecioNoche().multiply(BigDecimal.valueOf(dias));
        BigDecimal precioServicios = servicios.stream()
                .map(rs -> rs.getPrecioUnitario().multiply(BigDecimal.valueOf(rs.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return precioAlojamiento.add(precioServicios);
    }

    public void cambiarEstado(int idReserva, EstadoReserva nuevoEstado) {
        Reserva reserva = reservaDAO.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        EstadoReserva actual = reserva.getEstado();
        boolean valida = switch (actual) {
            case PENDIENTE   -> nuevoEstado == EstadoReserva.CONFIRMADA || nuevoEstado == EstadoReserva.CANCELADA;
            case CONFIRMADA  -> nuevoEstado == EstadoReserva.ACTIVA     || nuevoEstado == EstadoReserva.CANCELADA;
            case ACTIVA      -> nuevoEstado == EstadoReserva.COMPLETADA || nuevoEstado == EstadoReserva.CANCELADA;
            case COMPLETADA, CANCELADA -> false;
        };
        if (!valida) {
            throw new IllegalStateException("Transición no permitida: " + actual + " → " + nuevoEstado);
        }
        reservaDAO.cambiarEstado(idReserva, nuevoEstado);
    }

    public List<Reserva> obtenerReservasCliente(int idCliente) {
        return reservaDAO.findByCliente(idCliente);
    }
}
