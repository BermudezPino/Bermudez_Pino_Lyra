package com.lyra.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cabana {
    private int id;
    private String nombre;
    private String descripcion;
    private int capacidad;
    private BigDecimal precioNoche;
    private boolean disponible;
    private LocalDateTime fechaCreacion;
}
