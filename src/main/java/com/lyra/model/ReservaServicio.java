package com.lyra.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaServicio {
    private int idReserva;
    private int idServicioExtra;
    private int cantidad;
    private BigDecimal precioUnitario;
}
