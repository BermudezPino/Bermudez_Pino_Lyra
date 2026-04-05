package com.lyra.service;

import com.lyra.database.ClienteDAO;
import com.lyra.model.Cliente;

import java.util.List;
import java.util.Optional;

public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public List<Cliente> findAll() {
        return clienteDAO.findAll();
    }

    public Optional<Cliente> findById(int id) {
        return clienteDAO.findById(id);
    }

    public Optional<Cliente> findByDni(String dni) {
        return clienteDAO.findByDni(dni);
    }

    public void save(Cliente c) {
        validar(c);
        clienteDAO.save(c);
    }

    public void update(Cliente c) {
        validar(c);
        clienteDAO.update(c);
    }

    public void delete(int id) {
        clienteDAO.delete(id);
    }

    private void validar(Cliente c) {
        if (c.getDni() == null || c.getDni().isBlank()) {
            throw new IllegalArgumentException("El DNI del cliente no puede estar en blanco.");
        }
        if (c.getEmail() == null || c.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email del cliente no puede estar en blanco.");
        }
    }
}
