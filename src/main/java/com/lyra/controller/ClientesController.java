package com.lyra.controller;

import com.lyra.database.ClienteDAO;
import com.lyra.model.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientesController implements Initializable {

    @FXML private TableView<Cliente>           tablaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String>  colNombre;
    @FXML private TableColumn<Cliente, String>  colApellidos;
    @FXML private TableColumn<Cliente, String>  colDni;
    @FXML private TableColumn<Cliente, String>  colEmail;
    @FXML private TableColumn<Cliente, String>  colTelefono;

    @FXML private TextField tfNombre;
    @FXML private TextField tfApellidos;
    @FXML private TextField tfDni;
    @FXML private TextField tfEmail;
    @FXML private TextField tfTelefono;

    private int idSeleccionado = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> { if (newVal != null) rellenarFormulario(newVal); }
        );

        cargarTabla();
    }

    private void cargarTabla() {
        ObservableList<Cliente> datos = FXCollections.observableArrayList(new ClienteDAO().findAll());
        tablaClientes.setItems(datos);
    }

    private void rellenarFormulario(Cliente c) {
        idSeleccionado = c.getId();
        tfNombre.setText(c.getNombre());
        tfApellidos.setText(c.getApellidos());
        tfDni.setText(c.getDni());
        tfEmail.setText(c.getEmail());
        tfTelefono.setText(c.getTelefono() != null ? c.getTelefono() : "");
    }

    @FXML
    private void nuevoCliente() {
        limpiarFormulario();
        tablaClientes.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardarCliente() {
        if (tfNombre.getText().isBlank() || tfApellidos.getText().isBlank()
                || tfDni.getText().isBlank() || tfEmail.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Nombre, apellidos, DNI y email son obligatorios.").showAndWait();
            return;
        }

        Cliente c = new Cliente();
        c.setNombre(tfNombre.getText().trim());
        c.setApellidos(tfApellidos.getText().trim());
        c.setDni(tfDni.getText().trim());
        c.setEmail(tfEmail.getText().trim());
        c.setTelefono(tfTelefono.getText().trim());

        ClienteDAO dao = new ClienteDAO();
        if (idSeleccionado == 0) {
            dao.save(c);
        } else {
            c.setId(idSeleccionado);
            dao.update(c);
        }
        limpiarFormulario();
        cargarTabla();
    }

    @FXML
    private void eliminarCliente() {
        if (idSeleccionado == 0) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un cliente de la tabla.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar este cliente?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new ClienteDAO().delete(idSeleccionado);
            limpiarFormulario();
            cargarTabla();
        }
    }

    @FXML
    private void limpiarFormulario() {
        idSeleccionado = 0;
        tfNombre.clear();
        tfApellidos.clear();
        tfDni.clear();
        tfEmail.clear();
        tfTelefono.clear();
    }
}
