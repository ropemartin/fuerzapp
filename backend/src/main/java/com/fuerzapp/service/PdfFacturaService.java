package com.fuerzapp.service;

import com.fuerzapp.dto.FacturaLineaResponse;
import com.fuerzapp.dto.FacturaResponse;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfFacturaService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Color COLOR_PRIMARIO  = new Color(25, 118, 210);   // #1976d2
    private static final Color COLOR_CABECERA  = new Color(25, 118, 210);
    private static final Color COLOR_FILA_PAR  = new Color(240, 247, 255);
    private static final Color COLOR_TOTAL     = new Color(232, 245, 253);

    public byte[] generar(FacturaResponse factura) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font fontTitulo   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   20, COLOR_PRIMARIO);
            Font fontSeccion  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   11, Color.WHITE);
            Font fontLabel    = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   9, Color.DARK_GRAY);
            Font fontValor    = FontFactory.getFont(FontFactory.HELVETICA,         9, Color.DARK_GRAY);
            Font fontTHdr     = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   9, Color.WHITE);
            Font fontTCel     = FontFactory.getFont(FontFactory.HELVETICA,         9, Color.DARK_GRAY);
            Font fontTTotal   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   9, COLOR_PRIMARIO);
            Font fontPie      = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7, Color.GRAY);

            // ── Cabecera: logo izquierda + datos empresa derecha ──────────────

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 1});

            // Nombre gimnasio / logo (solo texto si no hay imagen)
            PdfPCell celdaLogo = new PdfPCell();
            celdaLogo.setBorder(Rectangle.NO_BORDER);
            celdaLogo.setPaddingBottom(10);
            Paragraph pNombreGym = new Paragraph(
                    nvl(factura.getGimnasioNombre(), "Gimnasio"),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COLOR_PRIMARIO)
            );
            celdaLogo.addElement(pNombreGym);
            headerTable.addCell(celdaLogo);

            // Datos empresa alineados a la derecha
            PdfPCell celdaDatosEmpresa = new PdfPCell();
            celdaDatosEmpresa.setBorder(Rectangle.NO_BORDER);
            celdaDatosEmpresa.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaDatosEmpresa.setPaddingBottom(10);
            if (factura.getGimnasioDireccion() != null) {
                celdaDatosEmpresa.addElement(parRalineada(factura.getGimnasioDireccion(), fontValor));
            }
            if (factura.getGimnasioTelefono() != null) {
                celdaDatosEmpresa.addElement(parRalineada(factura.getGimnasioTelefono(), fontValor));
            }
            if (factura.getGimnasioEmail() != null) {
                celdaDatosEmpresa.addElement(parRalineada(factura.getGimnasioEmail(), fontValor));
            }
            headerTable.addCell(celdaDatosEmpresa);

            doc.add(headerTable);

            // ── Línea separadora ──────────────────────────────────────────────

            addLinea(doc, COLOR_PRIMARIO);

            // ── Título FACTURA + número ───────────────────────────────────────

            Paragraph titulo = new Paragraph("FACTURA", fontTitulo);
            titulo.setSpacingBefore(12);
            titulo.setSpacingAfter(2);
            doc.add(titulo);

            doc.add(new Paragraph(
                    "N.º " + nvl(factura.getNumeroFactura(), "—") + "    ·    " +
                    (factura.getFechaEmision() != null ? factura.getFechaEmision().format(FMT) : ""),
                    FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY)
            ));

            // ── Bloque datos cliente ──────────────────────────────────────────

            PdfPTable bloques = new PdfPTable(2);
            bloques.setWidthPercentage(100);
            bloques.setWidths(new float[]{1, 1});
            bloques.setSpacingBefore(16);
            bloques.setSpacingAfter(16);

            bloques.addCell(bloqueInfo("FACTURADO A",
                    nvl(factura.getClienteNombre(), "—") + "\n" +
                    nvl(factura.getClienteEmail(), ""),
                    fontSeccion, fontValor));

            bloques.addCell(bloqueInfo("SERVICIO",
                    nvl(factura.getTipoSuscripcionNombre(), "—"),
                    fontSeccion, fontValor));

            doc.add(bloques);

            // ── Tabla de líneas ───────────────────────────────────────────────

            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{5, 1.2f, 1.8f, 1.8f});

            String[] hdr = {"Descripción", "Cant.", "Precio unit.", "Subtotal"};
            int[] hdrAlign = {Element.ALIGN_LEFT, Element.ALIGN_CENTER, Element.ALIGN_RIGHT, Element.ALIGN_RIGHT};
            for (int i = 0; i < hdr.length; i++) {
                PdfPCell c = new PdfPCell(new Phrase(hdr[i], fontTHdr));
                c.setBackgroundColor(COLOR_CABECERA);
                c.setHorizontalAlignment(hdrAlign[i]);
                c.setPadding(6);
                c.setBorder(Rectangle.NO_BORDER);
                tabla.addCell(c);
            }

            if (factura.getLineas() != null) {
                int row = 0;
                for (FacturaLineaResponse l : factura.getLineas()) {
                    Color bg = (row++ % 2 == 0) ? Color.WHITE : COLOR_FILA_PAR;
                    tabla.addCell(celda(nvl(l.getDescripcion(), "—"), fontTCel, Element.ALIGN_LEFT, bg));
                    tabla.addCell(celda(String.valueOf(l.getCantidad()), fontTCel, Element.ALIGN_CENTER, bg));
                    tabla.addCell(celda(fmt(l.getPrecioUnitario()), fontTCel, Element.ALIGN_RIGHT, bg));
                    tabla.addCell(celda(fmt(l.getSubtotal()), fontTCel, Element.ALIGN_RIGHT, bg));
                }
            }

            // Fila base imponible
            tabla.addCell(celdaSpan("", fontTCel, 2, Color.WHITE));
            tabla.addCell(celda("Base imponible", fontLabel, Element.ALIGN_RIGHT, COLOR_TOTAL));
            tabla.addCell(celda(fmt(factura.getBaseImponible()), fontTCel, Element.ALIGN_RIGHT, COLOR_TOTAL));

            // Fila IVA
            String labelIva = "IVA (" + nvl(factura.getPorcentajeIva(), 0) + "%)";
            tabla.addCell(celdaSpan("", fontTCel, 2, Color.WHITE));
            tabla.addCell(celda(labelIva, fontLabel, Element.ALIGN_RIGHT, COLOR_TOTAL));
            tabla.addCell(celda(fmt(factura.getImporteIva()), fontTCel, Element.ALIGN_RIGHT, COLOR_TOTAL));

            // Fila total
            tabla.addCell(celdaSpan("", fontTCel, 2, Color.WHITE));
            tabla.addCell(celda("TOTAL", fontTTotal, Element.ALIGN_RIGHT, COLOR_TOTAL));
            tabla.addCell(celda(fmt(factura.getImporteTotal()), fontTTotal, Element.ALIGN_RIGHT, COLOR_TOTAL));

            doc.add(tabla);

            // ── Pie de página ─────────────────────────────────────────────────

            addLinea(doc, Color.LIGHT_GRAY);
            Paragraph pie = new Paragraph(
                    "Documento generado automáticamente. Conserve esta factura para sus registros.",
                    fontPie
            );
            pie.setAlignment(Element.ALIGN_CENTER);
            pie.setSpacingBefore(6);
            doc.add(pie);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de factura: " + e.getMessage(), e);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private PdfPCell bloqueInfo(String titulo, String contenido, Font fontTitulo, Font fontContenido) {
        PdfPTable inner = new PdfPTable(1);
        inner.setWidthPercentage(100);

        PdfPCell head = new PdfPCell(new Phrase(titulo, fontTitulo));
        head.setBackgroundColor(COLOR_CABECERA);
        head.setBorder(Rectangle.NO_BORDER);
        head.setPadding(6);
        inner.addCell(head);

        PdfPCell body = new PdfPCell(new Phrase(contenido, fontContenido));
        body.setBackgroundColor(new Color(245, 245, 245));
        body.setBorder(Rectangle.NO_BORDER);
        body.setPadding(6);
        inner.addCell(body);

        PdfPCell wrapper = new PdfPCell();
        wrapper.setBorder(Rectangle.NO_BORDER);
        wrapper.setPadding(0);
        wrapper.addElement(inner);
        return wrapper;
    }

    private PdfPCell celda(String texto, Font font, int align, Color bg) {
        PdfPCell c = new PdfPCell(new Phrase(texto, font));
        c.setHorizontalAlignment(align);
        c.setBackgroundColor(bg);
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(5);
        return c;
    }

    private PdfPCell celdaSpan(String texto, Font font, int span, Color bg) {
        PdfPCell c = new PdfPCell(new Phrase(texto, font));
        c.setColspan(span);
        c.setBackgroundColor(bg);
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(5);
        return c;
    }

    private Paragraph parRalineada(String texto, Font font) {
        Paragraph p = new Paragraph(texto, font);
        p.setAlignment(Element.ALIGN_RIGHT);
        return p;
    }

    private void addLinea(Document doc, Color color) throws DocumentException {
        PdfPTable linea = new PdfPTable(1);
        linea.setWidthPercentage(100);
        linea.setSpacingBefore(4);
        linea.setSpacingAfter(4);
        PdfPCell c = new PdfPCell();
        c.setFixedHeight(2);
        c.setBackgroundColor(color);
        c.setBorder(Rectangle.NO_BORDER);
        linea.addCell(c);
        doc.add(linea);
    }

    private String fmt(java.math.BigDecimal v) {
        if (v == null) return "0,00 €";
        return String.format("%.2f €", v).replace('.', ',');
    }

    private String nvl(String s, String def) {
        return (s != null && !s.isBlank()) ? s : def;
    }

    private int nvl(Integer v, int def) {
        return v != null ? v : def;
    }
}
