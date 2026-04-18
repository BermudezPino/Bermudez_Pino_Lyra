package com.lyra.utils;

import com.lyra.model.Cabana;
import com.lyra.model.Cliente;
import com.lyra.model.Reserva;
import com.lyra.model.ReservaServicio;
import com.lyra.model.ServicioExtra;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XmlExporter {

    public static void exportarReserva(
            Reserva reserva,
            Cliente cliente,
            Cabana cabana,
            List<ReservaServicio> servicios,
            List<ServicioExtra> catalogoServicios,
            String rutaArchivo) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("reserva");
        root.setAttribute("id", String.valueOf(reserva.getId()));
        doc.appendChild(root);

        Element eCliente = doc.createElement("cliente");
        root.appendChild(eCliente);
        if (cliente != null) {
            appendText(doc, eCliente, "nombre", cliente.getNombre() != null ? cliente.getNombre() : "");
            appendText(doc, eCliente, "apellidos", cliente.getApellidos() != null ? cliente.getApellidos() : "");
            appendText(doc, eCliente, "dni", cliente.getDni() != null ? cliente.getDni() : "");
            appendText(doc, eCliente, "email", cliente.getEmail() != null ? cliente.getEmail() : "");
        }

        Element eCabana = doc.createElement("cabana");
        root.appendChild(eCabana);
        if (cabana != null) {
            appendText(doc, eCabana, "nombre", cabana.getNombre() != null ? cabana.getNombre() : "");
            appendText(doc, eCabana, "capacidad", String.valueOf(cabana.getCapacidad()));
            appendText(doc, eCabana, "precioNoche", cabana.getPrecioNoche() != null ? cabana.getPrecioNoche().toPlainString() : "0.00");
        }

        appendText(doc, root, "fechaEntrada", reserva.getFechaEntrada().toString());
        appendText(doc, root, "fechaSalida", reserva.getFechaSalida().toString());
        appendText(doc, root, "estado", reserva.getEstado().name());

        Map<Integer, ServicioExtra> catalogoMap = catalogoServicios.stream()
                .collect(Collectors.toMap(ServicioExtra::getId, s -> s));

        Element eServicios = doc.createElement("servicios");
        root.appendChild(eServicios);

        if (servicios != null) for (ReservaServicio rs : servicios) {
            ServicioExtra se = catalogoMap.get(rs.getIdServicioExtra());
            if (se == null) continue;

            BigDecimal subtotal = rs.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(rs.getCantidad()));

            Element eServicio = doc.createElement("servicio");
            eServicio.setAttribute("cantidad", String.valueOf(rs.getCantidad()));
            eServicio.setAttribute("precioUnitario", rs.getPrecioUnitario().toPlainString());
            eServicios.appendChild(eServicio);

            appendText(doc, eServicio, "nombre", se.getNombre());
            appendText(doc, eServicio, "subtotal", subtotal.toPlainString());
        }

        appendText(doc, root, "precioTotal",
                reserva.getPrecioTotal() != null ? reserva.getPrecioTotal().toPlainString() : "0.00");

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        File archivo = new File(rutaArchivo);
        File padre = archivo.getParentFile();
        if (padre != null) padre.mkdirs();
        transformer.transform(new DOMSource(doc), new StreamResult(archivo));
    }

    private static void appendText(Document doc, Element parent, String tag, String value) {
        Element el = doc.createElement(tag);
        el.setTextContent(value);
        parent.appendChild(el);
    }
}
