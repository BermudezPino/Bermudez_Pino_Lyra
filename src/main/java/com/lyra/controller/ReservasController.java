package com.lyra.controller;

import com.lyra.database.CabanaDAO;
import com.lyra.database.ClienteDAO;
import com.lyra.database.ReservaDAO;
import com.lyra.database.ServicioExtraDAO;
import com.lyra.model.*;
import com.lyra.service.ReservaService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReservasController implements Initializable {

    @FXML private TableView<Reserva>              tablaReservas;
    @FXML private TableColumn<Reserva, Integer>   colId;
    @FXML private TableColumn<Reserva, String>    colCliente;
    @FXML private TableColumn<Reserva, String>    colCabana;
    @FXML private TableColumn<Reserva, LocalDate> colEntrada;
    @FXML private TableColumn<Reserva, LocalDate> colSalida;
    @FXML private TableColumn<Reserva, EstadoReserva> colEstado;
    @FXML private TableColumn<Reserva, BigDecimal> colPrecioTotal;

    @FXML private ComboBox<Cliente>       cbCliente;
    @FXML private ComboBox<Cabana>        cbCabana;
    @FXML private DatePicker              dpEntrada;
    @FXML private DatePicker              dpSalida;
    @FXML private ComboBox<EstadoReserva> cbEstado;
    @FXML private TextArea                taObservaciones;

    @FXML private ListView<ServicioExtra>             listaServicios;
    @FXML private TextField                           tfCantidadServicio;
    @FXML private TableView<ReservaServicio>          tablaServiciosReserva;
    @FXML private TableColumn<ReservaServicio, String>     colServNombre;
    @FXML private TableColumn<ReservaServicio, Integer>    colServCantidad;
    @FXML private TableColumn<ReservaServicio, BigDecimal> colServPrecio;

    private final ReservaDAO        reservaDAO        = new ReservaDAO();
    private final ClienteDAO        clienteDAO        = new ClienteDAO();
    private final CabanaDAO         cabanaDAO         = new CabanaDAO();
    private final ServicioExtraDAO  servicioExtraDAO  = new ServicioExtraDAO();

    private List<Cliente>       listaClientes;
    private List<Cabana>        listaCabanas;
    private List<ServicioExtra> todosServicios;
    private List<ReservaServicio> serviciosAnadidos = new ArrayList<>();
    private int idSeleccionado = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaClientes  = clienteDAO.findAll();
        listaCabanas   = cabanaDAO.findAll();
        todosServicios = servicioExtraDAO.findAll();

        cbCliente.setItems(FXCollections.observableArrayList(listaClientes));
        cbCliente.setCellFactory(lv -> clienteCell());
        cbCliente.setButtonCell(clienteCell());

        cbCabana.setItems(FXCollections.observableArrayList(listaCabanas));
        cbCabana.setCellFactory(lv -> cabanaCell());
        cbCabana.setButtonCell(cabanaCell());

        cbEstado.setItems(FXCollections.observableArrayList(EstadoReserva.values()));

        listaServicios.setItems(FXCollections.observableArrayList(todosServicios));
        listaServicios.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(ServicioExtra item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " (" + item.getPrecio() + "€)");
            }
        });

        colServNombre.setCellValueFactory(data -> {
            int id = data.getValue().getIdServicioExtra();
            String nombre = todosServicios.stream()
                    .filter(s -> s.getId() == id).map(ServicioExtra::getNombre)
                    .findFirst().orElse("—");
            return new SimpleStringProperty(nombre);
        });
        colServCantidad.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getCantidad()).asObject());
        colServPrecio.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getPrecioUnitario()));

        configurarTabla();
        cargarTabla();

        tablaReservas.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> { if (newVal != null) rellenarFormulario(newVal); });
    }

    private ListCell<Cliente> clienteCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNombre() + " " + c.getApellidos());
            }
        };
    }

    private ListCell<Cabana> cabanaCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Cabana c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNombre());
            }
        };
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(data -> {
            int id = data.getValue().getIdCliente();
            return new SimpleStringProperty(listaClientes.stream()
                    .filter(c -> c.getId() == id)
                    .map(c -> c.getNombre() + " " + c.getApellidos())
                    .findFirst().orElse("—"));
        });
        colCabana.setCellValueFactory(data -> {
            int id = data.getValue().getIdCabana();
            return new SimpleStringProperty(listaCabanas.stream()
                    .filter(c -> c.getId() == id).map(Cabana::getNombre)
                    .findFirst().orElse("—"));
        });
        colEntrada.setCellValueFactory(new PropertyValueFactory<>("fechaEntrada"));
        colSalida.setCellValueFactory(new PropertyValueFactory<>("fechaSalida"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colPrecioTotal.setCellValueFactory(new PropertyValueFactory<>("precioTotal"));
        colPrecioTotal.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f€", item));
            }
        });
    }

    private void cargarTabla() {
        tablaReservas.setItems(FXCollections.observableArrayList(reservaDAO.findAll()));
    }

    private void rellenarFormulario(Reserva r) {
        idSeleccionado = r.getId();
        cbCliente.getItems().stream().filter(c -> c.getId() == r.getIdCliente()).findFirst().ifPresent(cbCliente::setValue);
        cbCabana.getItems().stream().filter(c -> c.getId() == r.getIdCabana()).findFirst().ifPresent(cbCabana::setValue);
        dpEntrada.setValue(r.getFechaEntrada());
        dpSalida.setValue(r.getFechaSalida());
        cbEstado.setValue(r.getEstado());
        taObservaciones.setText(r.getObservaciones());
    }

    @FXML private void nuevaReserva() { limpiarFormulario(); }

    @FXML private void añadirServicio() {
        ServicioExtra sel = listaServicios.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Sin selección", "Selecciona un servicio."); return; }
        int cantidad;
        try {
            cantidad = Integer.parseInt(tfCantidadServicio.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            alerta("Cantidad inválida", "Introduce un entero mayor que 0.");
            return;
        }
        ReservaServicio rs = new ReservaServicio();
        rs.setIdServicioExtra(sel.getId());
        rs.setCantidad(cantidad);
        rs.setPrecioUnitario(sel.getPrecio());
        serviciosAnadidos.add(rs);
        tablaServiciosReserva.setItems(FXCollections.observableArrayList(serviciosAnadidos));
        tfCantidadServicio.clear();
    }

    @FXML private void quitarServicio() {
        ReservaServicio sel = tablaServiciosReserva.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        serviciosAnadidos.remove(sel);
        tablaServiciosReserva.setItems(FXCollections.observableArrayList(serviciosAnadidos));
    }

    @FXML private void guardarReserva() {
        if (cbCliente.getValue() == null || cbCabana.getValue() == null
                || dpEntrada.getValue() == null || dpSalida.getValue() == null
                || cbEstado.getValue() == null) {
            alerta("Campos obligatorios", "Cliente, cabaña, fechas y estado son obligatorios.");
            return;
        }
        if (!dpSalida.getValue().isAfter(dpEntrada.getValue())) {
            alerta("Fechas inválidas", "La fecha de salida debe ser posterior a la de entrada.");
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
                new ReservaService().crearReserva(r, serviciosAnadidos);
            } else {
                r.setId(idSeleccionado);
                reservaDAO.update(r);
            }
            cargarTabla();
            limpiarFormulario();
        } catch (Exception e) {
            alerta("Error al guardar", e.getMessage());
        }
    }

    @FXML private void cambiarEstado() {
        Reserva sel = tablaReservas.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Sin selección", "Selecciona una reserva."); return; }
        ChoiceDialog<EstadoReserva> dialog = new ChoiceDialog<>(sel.getEstado(), EstadoReserva.values());
        dialog.setTitle("Cambiar estado");
        dialog.setHeaderText("Reserva #" + sel.getId());
        dialog.setContentText("Nuevo estado:");
        dialog.showAndWait().ifPresent(nuevo -> {
            try {
                new ReservaService().cambiarEstado(sel.getId(), nuevo);
                cargarTabla();
                limpiarFormulario();
            } catch (Exception e) {
                alerta("Error", e.getMessage());
            }
        });
    }

    @FXML private void eliminarReserva() {
        Reserva sel = tablaReservas.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Sin selección", "Selecciona una reserva."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar reserva #" + sel.getId() + "?")
                .showAndWait().ifPresent(r -> {
                    if (r == ButtonType.OK) {
                        reservaDAO.delete(sel.getId());
                        cargarTabla();
                        limpiarFormulario();
                    }
                });
    }

    @FXML public void exportarXml() {
        if (idSeleccionado == 0) {
            alerta("Sin selección", "Selecciona una reserva de la tabla antes de exportar.");
            return;
        }
        Reserva reserva = reservaDAO.findById(idSeleccionado).orElse(null);
        if (reserva == null) return;
        if (reserva.getPrecioTotal() == null) {
            alerta("Precio no calculado", "La reserva #" + idSeleccionado + " no tiene precio total calculado.");
            return;
        }
        com.lyra.model.Cliente cliente = clienteDAO.findById(reserva.getIdCliente()).orElse(null);
        com.lyra.model.Cabana cabana = cabanaDAO.findById(reserva.getIdCabana()).orElse(null);
        List<ReservaServicio> servicios = new com.lyra.database.ReservaServicioDAO().findByReserva(idSeleccionado);
        List<ServicioExtra> catalogo = servicioExtraDAO.findAll();

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar XML de reserva");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("XML (*.xml)", "*.xml"));
        fileChooser.setInitialFileName("reserva_" + idSeleccionado + ".xml");

        java.io.File archivo = fileChooser.showSaveDialog(tablaReservas.getScene().getWindow());
        if (archivo == null) return;
        try {
            com.lyra.utils.XmlExporter.exportarReserva(reserva, cliente, cabana, servicios, catalogo, archivo.getAbsolutePath());
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Exportación completada");
            ok.setHeaderText(null);
            ok.setContentText("XML exportado en: " + archivo.getAbsolutePath());
            ok.showAndWait();
        } catch (Exception e) {
            alerta("Error al exportar XML", e.getMessage());
        }
    }

    @FXML public void limpiarFormulario() {
        idSeleccionado = 0;
        cbCliente.setValue(null); cbCabana.setValue(null);
        dpEntrada.setValue(null); dpSalida.setValue(null);
        cbEstado.setValue(null);  taObservaciones.clear();
        serviciosAnadidos = new ArrayList<>();
        tablaServiciosReserva.setItems(FXCollections.observableArrayList(serviciosAnadidos));
        tablaReservas.getSelectionModel().clearSelection();
    }

    private void alerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
