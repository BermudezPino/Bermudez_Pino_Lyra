package com.lyra.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaEmpleado {
    private int idReserva;
    private int idEmpleado;
    private String rol;
    private LocalDateTime fechaAccion;
    private String notas;
}
