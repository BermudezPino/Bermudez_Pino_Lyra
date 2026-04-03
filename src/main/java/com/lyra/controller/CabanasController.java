package com.lyra.controller;

import com.lyra.database.CabanaDAO;
import com.lyra.model.Cabana;
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

public class CabanasController implements Initializable {

    @FXML private TableView<Cabana>            tablaCabanas;
    @FXML private TableColumn<Cabana, Integer>    colId;
    @FXML private TableColumn<Cabana, String>     colNombre;
    @FXML private TableColumn<Cabana, Integer>    colCapacidad;
    @FXML private TableColumn<Cabana, BigDecimal> colPrecio;
    @FXML private TableColumn<Cabana, Boolean>    colDisponible;

    @FXML private TextField tfNombre;
    @FXML private TextArea  taDescripcion;
    @FXML private TextField tfCapacidad;
    @FXML private TextField tfPrecio;
    @FXML private CheckBox  cbDisponible;

    private int idSeleccionado = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioNoche"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        tablaCabanas.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> { if (newVal != null) rellenarFormulario(newVal); }
        );

        cargarTabla();
    }

    private void cargarTabla() {
        ObservableList<Cabana> datos = FXCollections.observableArrayList(new CabanaDAO().findAll());
        tablaCabanas.setItems(datos);
    }

    private void rellenarFormulario(Cabana c) {
        idSeleccionado = c.getId();
        tfNombre.setText(c.getNombre());
        taDescripcion.setText(c.getDescripcion() != null ? c.getDescripcion() : "");
        tfCapacidad.setText(String.valueOf(c.getCapacidad()));
        tfPrecio.setText(c.getPrecioNoche().toPlainString());
        cbDisponible.setSelected(c.isDisponible());
    }

    @FXML
    private void nuevaCabana() {
        limpiarFormulario();
        tablaCabanas.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardarCabana() {
        if (tfNombre.getText().isBlank() || tfCapacidad.getText().isBlank() || tfPrecio.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Nombre, capacidad y precio son obligatorios.").showAndWait();
            return;
        }
        int capacidad;
        BigDecimal precio;
        try {
            capacidad = Integer.parseInt(tfCapacidad.getText().trim());
            precio = new BigDecimal(tfPrecio.getText().trim());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Capacidad debe ser entero y precio un número decimal.").showAndWait();
            return;
        }

        Cabana c = new Cabana();
        c.setNombre(tfNombre.getText().trim());
        c.setDescripcion(taDescripcion.getText().trim());
        c.setCapacidad(capacidad);
        c.setPrecioNoche(precio);
        c.setDisponible(cbDisponible.isSelected());

        CabanaDAO dao = new CabanaDAO();
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
    private void eliminarCabana() {
        if (idSeleccionado == 0) {
            new Alert(Alert.AlertType.WARNING, "Selecciona una cabaña de la tabla.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar esta cabaña?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new CabanaDAO().delete(idSeleccionado);
            limpiarFormulario();
            cargarTabla();
        }
    }

    @FXML
    private void limpiarFormulario() {
        idSeleccionado = 0;
        tfNombre.clear();
        taDescripcion.clear();
        tfCapacidad.clear();
        tfPrecio.clear();
        cbDisponible.setSelected(false);
    }
}
