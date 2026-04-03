package com.lyra.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane contenido;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void mostrarCabanas() {
        cargarVista("/fxml/CabanasView.fxml");
    }

    @FXML
    private void mostrarClientes() {
        cargarVista("/fxml/ClientesView.fxml");
    }

    @FXML
    private void mostrarReservas() {
        cargarVista("/fxml/ReservasView.fxml");
    }

    @FXML
    private void mostrarServicios() {
        cargarVista("/fxml/ServiciosView.fxml");
    }

    @FXML
    private void mostrarEmpleados() {
        cargarVista("/fxml/EmpleadosView.fxml");
    }

    private void cargarVista(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contenido.getChildren().setAll(node);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo cargar la vista");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
