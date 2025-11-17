package com.example.Hirelance.services;

import com.example.Hirelance.models.Contrato;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfService {

    public void exportarContratoPdf(HttpServletResponse response, Contrato contrato) throws IOException {
        // 1. Configurar la respuesta HTTP
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Contrato_" + contrato.getIdContrato() + ".pdf";
        response.setHeader(headerKey, headerValue);

        // 2. Crear el documento PDF (Tamaño carta, márgenes estándar)
        Document document = new Document(PageSize.LETTER);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // --- FUENTES ---
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        Font fontTexto = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Color.BLACK);
        Font fontNegrita = FontFactory.getFont(FontFactory.TIMES_BOLD, 11, Color.BLACK);
        Font fontMini = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);

        // --- FORMATOS ---
        DateTimeFormatter fechaFmt = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        NumberFormat dineroFmt = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

        // --- CONTENIDO DEL DOCUMENTO ---

        // 1. Encabezado / Logo (Texto simple por ahora)
        Paragraph logo = new Paragraph("HIRELANCE PLATFORM", fontMini);
        logo.setAlignment(Element.ALIGN_RIGHT);
        document.add(logo);

        Paragraph espacio = new Paragraph("\n");
        document.add(espacio);

        // 2. Título
        Paragraph titulo = new Paragraph("CONTRATO DE PRESTACIÓN DE SERVICIOS", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph idContrato = new Paragraph("REF: CONTRATO #" + contrato.getIdContrato(), fontSubtitulo);
        idContrato.setAlignment(Element.ALIGN_CENTER);
        document.add(idContrato);

        document.add(espacio);
        document.add(espacio);

        // 3. Introducción (Partes)
        String textoIntro = "En la ciudad de " + (contrato.getContratista().getUbicaciones().isEmpty() ? "San Salvador" : contrato.getContratista().getUbicaciones().iterator().next().getCiudad()) +
                ", a los " + contrato.getFechaInicio().format(fechaFmt) + ", COMPARECEN:\n\n" +
                "Por una parte, " + contrato.getContratista().getNombre().toUpperCase() + " " + contrato.getContratista().getApellido().toUpperCase() +
                ", en adelante denominado EL CLIENTE.\n\n" +
                "Y por otra parte, " + contrato.getEstudiante().getNombre().toUpperCase() + " " + contrato.getEstudiante().getApellido().toUpperCase() +
                ", en adelante denominado EL FREELANCER.\n\n" +
                "Ambas partes acuerdan celebrar el presente contrato sujeto a las siguientes cláusulas:";

        Paragraph pIntro = new Paragraph(textoIntro, fontTexto);
        pIntro.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(pIntro);
        document.add(espacio);

        // 4. Cláusulas
        agregarClausula(document, "PRIMERA: OBJETO DEL CONTRATO",
                "El Freelancer se compromete a prestar los servicios profesionales para el proyecto titulado \"" + contrato.getProyecto().getTitulo().toUpperCase() + "\". " +
                        "La descripción del servicio es la siguiente: " + contrato.getProyecto().getDescripcion(), fontNegrita, fontTexto);

        agregarClausula(document, "SEGUNDA: HONORARIOS Y FORMA DE PAGO",
                "El Cliente pagará al Freelancer la suma total de " + dineroFmt.format(contrato.getTotalPago()) + " USD. " +
                        "El pago se realizará a través de los medios acordados en la plataforma Hirelance una vez entregado el trabajo a satisfacción.", fontNegrita, fontTexto);

        agregarClausula(document, "TERCERA: VIGENCIA",
                "El presente contrato inicia el " + contrato.getFechaInicio().format(fechaFmt) +
                        (contrato.getFechaFin() != null ? " y finalizará el " + contrato.getFechaFin().format(fechaFmt) + "." : " y tendrá una duración indefinida hasta la entrega del proyecto."),
                fontNegrita, fontTexto);

        agregarClausula(document, "CUARTA: CONFIDENCIALIDAD",
                "El Freelancer se obliga a mantener en secreto y no divulgar a terceros cualquier información confidencial del Cliente a la que tenga acceso durante la prestación de los servicios.", fontNegrita, fontTexto);

        agregarClausula(document, "QUINTA: PROPIEDAD INTELECTUAL",
                "Salvo pacto en contrario, los derechos patrimoniales sobre los trabajos realizados se transferirán al Cliente una vez cubierto el pago total de los honorarios.", fontNegrita, fontTexto);

        document.add(espacio);
        document.add(espacio);
        document.add(espacio);

        // 5. Firmas
        Paragraph firmas = new Paragraph("EN FE DE LO CUAL, las partes firman el presente documento:", fontTexto);
        firmas.setAlignment(Element.ALIGN_CENTER);
        document.add(firmas);

        document.add(espacio);
        document.add(espacio);
        document.add(espacio);
        document.add(espacio);

        // Tabla para alinear firmas lado a lado
        com.lowagie.text.Table tablaFirmas = new com.lowagie.text.Table(2);
        tablaFirmas.setBorderWidth(0);
        tablaFirmas.setWidth(100);
        tablaFirmas.setPadding(5);

        // Firma Cliente
        com.lowagie.text.Cell celdaCliente = new com.lowagie.text.Cell();
        celdaCliente.setBorder(0);
        celdaCliente.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaCliente.add(new Paragraph("___________________________\nEL CLIENTE\n" + contrato.getContratista().getNombre(), fontNegrita));
        tablaFirmas.addCell(celdaCliente);

        // Firma Freelancer
        com.lowagie.text.Cell celdaFreelancer = new com.lowagie.text.Cell();
        celdaFreelancer.setBorder(0);
        celdaFreelancer.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaFreelancer.add(new Paragraph("___________________________\nEL FREELANCER\n" + contrato.getEstudiante().getNombre(), fontNegrita));
        tablaFirmas.addCell(celdaFreelancer);

        document.add(tablaFirmas);

        // Pie de página automático
        Paragraph footer = new Paragraph("\n\nGenerado automáticamente por Hirelance System el " + java.time.LocalDate.now(), fontMini);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
    }

    private void agregarClausula(Document doc, String titulo, String contenido, Font fontTitulo, Font fontCuerpo) throws DocumentException {
        Paragraph pTitulo = new Paragraph(titulo, fontTitulo);
        doc.add(pTitulo);
        Paragraph pContenido = new Paragraph(contenido, fontCuerpo);
        pContenido.setAlignment(Element.ALIGN_JUSTIFIED);
        pContenido.setSpacingAfter(10);
        doc.add(pContenido);
    }
}