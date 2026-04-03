package com.lyra.controller;

import com.lyra.database.EmpleadoDAO;
import com.lyra.model.Empleado;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class EmpleadosController implements Initializable {

    @FXML private TableView<Empleado>           tablaEmpleados;
    @FXML private TableColumn<Empleado, Integer> colId;
    @FXML private TableColumn<Empleado, String>  colNombre;
    @FXML private TableColumn<Empleado, String>  colApellidos;
    @FXML private TableColumn<Empleado, String>  colCargo;
    @FXML private TableColumn<Empleado, String>  colActivo;

    @FXML private TextField  tfNombre;
    @FXML private TextField  tfApellidos;
    @FXML private TextField  tfDni;
    @FXML private TextField  tfEmail;
    @FXML private TextField  tfTelefono;
    @FXML private TextField  tfCargo;
    @FXML private DatePicker dpFechaContratacion;
    @FXML private CheckBox   cbActivo;

    private int idSeleccionado = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        colActivo.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().isActivo() ? "Sí" : "No")
        );

        tablaEmpleados.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> { if (newVal != null) rellenarFormulario(newVal); }
        );

        cargarTabla();
    }

    private void cargarTabla() {
        ObservableList<Empleado> datos = FXCollections.observableArrayList(new EmpleadoDAO().findAll());
        tablaEmpleados.setItems(datos);
    }

    private void rellenarFormulario(Empleado e) {
        idSeleccionado = e.getId();
        tfNombre.setText(e.getNombre());
        tfApellidos.setText(e.getApellidos());
        tfDni.setText(e.getDni());
        tfEmail.setText(e.getEmail());
        tfTelefono.setText(e.getTelefono() != null ? e.getTelefono() : "");
        tfCargo.setText(e.getCargo());
        dpFechaContratacion.setValue(e.getFechaContratacion());
        cbActivo.setSelected(e.isActivo());
    }

    @FXML
    private void nuevoEmpleado() {
        limpiarFormulario();
        tablaEmpleados.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardarEmpleado() {
        if (tfNombre.getText().isBlank() || tfApellidos.getText().isBlank()
                || tfDni.getText().isBlank() || tfCargo.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Nombre, apellidos, DNI y cargo son obligatorios.").showAndWait();
            return;
        }

        Empleado e = new Empleado();
        e.setNombre(tfNombre.getText().trim());
        e.setApellidos(tfApellidos.getText().trim());
        e.setDni(tfDni.getText().trim());
        e.setEmail(tfEmail.getText().trim());
        e.setTelefono(tfTelefono.getText().trim());
        e.setCargo(tfCargo.getText().trim());
        e.setFechaContratacion(dpFechaContratacion.getValue());
        e.setActivo(cbActivo.isSelected());

        EmpleadoDAO dao = new EmpleadoDAO();
        if (idSeleccionado == 0) {
            dao.save(e);
        } else {
            e.setId(idSeleccionado);
            dao.update(e);
        }
        limpiarFormulario();
        cargarTabla();
    }

    @FXML
    private void eliminarEmpleado() {
        if (idSeleccionado == 0) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un empleado de la tabla.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar este empleado?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new EmpleadoDAO().delete(idSeleccionado);
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
        tfCargo.clear();
        dpFechaContratacion.setValue(null);
        cbActivo.setSelected(true);
    }
}
