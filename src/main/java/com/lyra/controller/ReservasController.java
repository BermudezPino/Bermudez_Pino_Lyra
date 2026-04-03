package com.lyra.controller;

import com.lyra.database.CabanaDAO;
import com.lyra.database.ClienteDAO;
import com.lyra.database.ReservaDAO;
import com.lyra.model.Cabana;
import com.lyra.model.Cliente;
import com.lyra.model.EstadoReserva;
import com.lyra.model.Reserva;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservasController implements Initializable {

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, Integer> colId;
    @FXML private TableColumn<Reserva, String> colCliente;
    @FXML private TableColumn<Reserva, String> colCabana;
    @FXML private TableColumn<Reserva, LocalDate> colEntrada;
    @FXML private TableColumn<Reserva, LocalDate> colSalida;
    @FXML private TableColumn<Reserva, EstadoReserva> colEstado;
    @FXML private TableColumn<Reserva, BigDecimal> colPrecioTotal;

    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Cabana> cbCabana;
    @FXML private DatePicker dpEntrada;
    @FXML private DatePicker dpSalida;
    @FXML private ComboBox<EstadoReserva> cbEstado;
    @FXML private TextArea taObservaciones;

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final CabanaDAO cabanaDAO = new CabanaDAO();

    private List<Cliente> listaClientes;
    private List<Cabana> listaCabanas;
    private int idSeleccionado = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listaClientes = clienteDAO.findAll();
        listaCabanas = cabanaDAO.findAll();

        cbCliente.setItems(FXCollections.observableArrayList(listaClientes));
        cbCliente.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " " + item.getApellidos());
            }
        });
        cbCliente.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " " + item.getApellidos());
            }
        });

        cbCabana.setItems(FXCollections.observableArrayList(listaCabanas));
        cbCabana.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cabana item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        cbCabana.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Cabana item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });

        cbEstado.setItems(FXCollections.observableArrayList(EstadoReserva.values()));

        configurarTabla();
        cargarTabla();

        tablaReservas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) rellenarFormulario(newVal);
                }
        );
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colCliente.setCellValueFactory(data -> {
            int idCliente = data.getValue().getIdCliente();
            String nombre = listaClientes.stream()
                    .filter(c -> c.getId() == idCliente)
                    .map(c -> c.getNombre() + " " + c.getApellidos())
                    .findFirst().orElse("—");
            return new SimpleStringProperty(nombre);
        });

        colCabana.setCellValueFactory(data -> {
            int idCabana = data.getValue().getIdCabana();
            String nombre = listaCabanas.stream()
                    .filter(c -> c.getId() == idCabana)
                    .map(Cabana::getNombre)
                    .findFirst().orElse("—");
            return new SimpleStringProperty(nombre);
        });

        colEntrada.setCellValueFactory(new PropertyValueFactory<>("fechaEntrada"));
        colSalida.setCellValueFactory(new PropertyValueFactory<>("fechaSalida"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colPrecioTotal.setCellValueFactory(new PropertyValueFactory<>("precioTotal"));
        colPrecioTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f€", item));
                }
            }
        });
    }

    private void cargarTabla() {
        ObservableList<Reserva> datos = FXCollections.observableArrayList(reservaDAO.findAll());
        tablaReservas.setItems(datos);
    }

    private void rellenarFormulario(Reserva r) {
        idSeleccionado = r.getId();
        cbCliente.getItems().stream()
                .filter(c -> c.getId() == r.getIdCliente())
                .findFirst().ifPresent(cbCliente::setValue);
        cbCabana.getItems().stream()
                .filter(c -> c.getId() == r.getIdCabana())
                .findFirst().ifPresent(cbCabana::setValue);
        dpEntrada.setValue(r.getFechaEntrada());
        dpSalida.setValue(r.getFechaSalida());
        cbEstado.setValue(r.getEstado());
        taObservaciones.setText(r.getObservaciones());
    }

    @FXML
    public void nuevaReserva() {
        limpiarFormulario();
    }

    @FXML
    public void guardarReserva() {
        if (cbCliente.getValue() == null || cbCabana.getValue() == null
                || dpEntrada.getValue() == null || dpSalida.getValue() == null
                || cbEstado.getValue() == null) {
            mostrarError("Campos obligatorios", "Cliente, cabaña, fechas y estado son obligatorios.");
            return;
        }

        Reserva r = new Reserva();
        r.setIdCliente(cbCliente.getValue().getId());
        r.setIdCabana(cbCabana.getValue().getId());
        r.setFechaEntrada(dpEntrada.getValue());
        r.setFechaSalida(dpSalida.getValue());
        r.setEstado(cbEstado.getValue());
        r.setObservaciones(taObservaciones.getText());

        try {
            if (idSeleccionado == 0) {
                reservaDAO.save(r);
            } else {
                r.setId(idSeleccionado);
                reservaDAO.update(r);
            }
            cargarTabla();
            limpiarFormulario();
        } catch (Exception e) {
            mostrarError("Error al guardar", e.getMessage());
        }
    }

    @FXML
    public void cambiarEstado() {
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Sin selección", "Selecciona una reserva de la tabla.");
            return;
        }

        ChoiceDialog<EstadoReserva> dialog = new ChoiceDialog<>(seleccionada.getEstado(),
                EstadoReserva.values());
        dialog.setTitle("Cambiar estado");
        dialog.setHeaderText("Reserva #" + seleccionada.getId());
        dialog.setContentText("Nuevo estado:");

        Optional<EstadoReserva> resultado = dialog.showAndWait();
        resultado.ifPresent(nuevoEstado -> {
            try {
                reservaDAO.cambiarEstado(seleccionada.getId(), nuevoEstado);
                cargarTabla();
                limpiarFormulario();
            } catch (Exception e) {
                mostrarError("Error al cambiar estado", e.getMessage());
            }
        });
    }

    @FXML
    public void eliminarReserva() {
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Sin selección", "Selecciona una reserva de la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar reserva #" + seleccionada.getId() + "?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        confirm.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    reservaDAO.delete(seleccionada.getId());
                    cargarTabla();
                    limpiarFormulario();
                } catch (Exception e) {
                    mostrarError("Error al eliminar", e.getMessage());
                }
            }
        });
    }

    @FXML
    public void limpiarFormulario() {
        idSeleccionado = 0;
        cbCliente.setValue(null);
        cbCabana.setValue(null);
        dpEntrada.setValue(null);
        dpSalida.setValue(null);
        cbEstado.setValue(null);
        taObservaciones.clear();
        tablaReservas.getSelectionModel().clearSelection();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
