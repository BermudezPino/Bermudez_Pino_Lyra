package com.lyra.utils;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.InputStream;

public class XmlValidator {

    public static boolean validar(String rutaXml, String rutaXsd) throws Exception {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try (InputStream xsdStream = XmlValidator.class.getResourceAsStream(rutaXsd)) {
            if (xsdStream != null) {
                schema = sf.newSchema(new javax.xml.transform.stream.StreamSource(xsdStream));
            } else {
                schema = sf.newSchema(new File(rutaXsd));
            }
        }
        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(new File(rutaXml)));
            return true;
        } catch (SAXException e) {
            return false;
        }
    }
}
