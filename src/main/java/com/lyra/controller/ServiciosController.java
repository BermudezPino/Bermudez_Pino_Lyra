package com.lyra.controller;

import com.lyra.database.ServicioExtraDAO;
import com.lyra.model.ServicioExtra;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ServiciosController implements Initializable {

    @FXML private TableView<ServicioExtra>              tablaServicios;
    @FXML private TableColumn<ServicioExtra, Integer>    colId;
    @FXML private TableColumn<ServicioExtra, String>     colNombre;
    @FXML private TableColumn<ServicioExtra, BigDecimal> colPrecio;
    @FXML private TableColumn<ServicioExtra, Boolean>    colDisponible;

    @FXML private TextField tfNombre;
    @FXML private TextArea  taDescripcion;
    @FXML private TextField tfPrecio;
    @FXML private CheckBox  cbDisponible;

    private int idSeleccionado = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        tablaServicios.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> { if (newVal != null) rellenarFormulario(newVal); }
        );

        cargarTabla();
    }

    private void cargarTabla() {
        ObservableList<ServicioExtra> datos = FXCollections.observableArrayList(new ServicioExtraDAO().findAll());
        tablaServicios.setItems(datos);
    }

    private void rellenarFormulario(ServicioExtra s) {
        idSeleccionado = s.getId();
        tfNombre.setText(s.getNombre());
        taDescripcion.setText(s.getDescripcion() != null ? s.getDescripcion() : "");
        tfPrecio.setText(s.getPrecio().toPlainString());
        cbDisponible.setSelected(s.isDisponible());
    }

    @FXML
    private void nuevoServicio() {
        limpiarFormulario();
        tablaServicios.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardarServicio() {
        if (tfNombre.getText().isBlank() || tfPrecio.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Nombre y precio son obligatorios.").showAndWait();
            return;
        }
        BigDecimal precio;
        try {
            precio = new BigDecimal(tfPrecio.getText().trim());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "El precio debe ser un número decimal válido.").showAndWait();
            return;
        }

        ServicioExtra s = new ServicioExtra();
        s.setNombre(tfNombre.getText().trim());
        s.setDescripcion(taDescripcion.getText().trim());
        s.setPrecio(precio);
        s.setDisponible(cbDisponible.isSelected());

        ServicioExtraDAO dao = new ServicioExtraDAO();
        if (idSeleccionado == 0) {
            dao.save(s);
        } else {
            s.setId(idSeleccionado);
            dao.update(s);
        }
        limpiarFormulario();
        cargarTabla();
    }

    @FXML
    private void eliminarServicio() {
        if (idSeleccionado == 0) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un servicio de la tabla.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar este servicio?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new ServicioExtraDAO().delete(idSeleccionado);
            limpiarFormulario();
            cargarTabla();
        }
    }

    @FXML
    private void limpiarFormulario() {
        idSeleccionado = 0;
        tfNombre.clear();
        taDescripcion.clear();
        tfPrecio.clear();
        cbDisponible.setSelected(false);
    }
}
