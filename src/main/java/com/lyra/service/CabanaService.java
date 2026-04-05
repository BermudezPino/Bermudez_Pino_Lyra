package com.lyra.service;

import com.lyra.database.CabanaDAO;
import com.lyra.database.ReservaDAO;
import com.lyra.model.Cabana;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CabanaService {

    private final CabanaDAO cabanaDAO = new CabanaDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();

    public List<Cabana> findAll() {
        return cabanaDAO.findAll();
    }

    public Optional<Cabana> findById(int id) {
        return cabanaDAO.findById(id);
    }

    public void save(Cabana c) {
        validar(c);
        cabanaDAO.save(c);
    }

    public void update(Cabana c) {
        validar(c);
        cabanaDAO.update(c);
    }

    public void delete(int id) {
        cabanaDAO.delete(id);
    }

    public List<Cabana> findDisponibles(LocalDate entrada, LocalDate salida) {
        List<Integer> ids = reservaDAO.findDisponibles(entrada, salida);
        return ids.stream()
                .map(cabanaDAO::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private void validar(Cabana c) {
        if (c.getPrecioNoche() == null || c.getPrecioNoche().signum() < 0) {
            throw new IllegalArgumentException("El precio por noche no puede ser negativo.");
        }
        if (c.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }
    }
}
