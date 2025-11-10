package reportes;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import mx.desarollo.dto.CajaReporteDTO;
import mx.desarollo.dto.PagoReporteDTO;
import mx.desarollo.dto.ProductoReporteDTO;
import mx.desarollo.dto.ReporteDiarioDTO;
import mx.desarollo.dto.RetiroReporteDTO;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class ReporteDiarioPDF {

    public void generarReporte(ReporteDiarioDTO datos, OutputStream outputStream) throws IOException {

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        //Logo
        URL logoURL = ReporteDiarioPDF.class.getResource("/reportes/rockon-icon.png");
        if (logoURL == null) {
            System.err.println("No se encontro el archivo de logo en /reportes/rockon-icon.png");
        } else {
            ImageData logoData = ImageDataFactory.create(logoURL);
            Image logo = new Image(logoData);
            logo.setWidth(100).setHeight(100);
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            doc.add(logo);
        }

        //Titulo
        String fechaFormateada = datos.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Paragraph titulo = new Paragraph("Reporte Diario " + fechaFormateada)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18)
                .setBold()
                .setMarginBottom(20);
        doc.add(titulo);

        //Productos
        crearSeccionEncabezado(doc, "Productos");
        Table tablaProductos = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2}));
        tablaProductos.setWidth(UnitValue.createPercentValue(100));
        tablaProductos.addHeaderCell(celdaHeader("Inventario"));
        tablaProductos.addHeaderCell(celdaHeader("Cantidad inicial"));
        tablaProductos.addHeaderCell(celdaHeader("Cantidad vendida"));
        tablaProductos.addHeaderCell(celdaHeader("Cantidad final"));

        for (ProductoReporteDTO prod : datos.getProductos()) {
            tablaProductos.addCell(celdaCentro(prod.getNombre()));
            tablaProductos.addCell(celdaCentro(prod.getStockInicial()));
            tablaProductos.addCell(celdaCentro(prod.getCantidadVendida()));
            tablaProductos.addCell(celdaCentro(prod.getStockFinal()));
        }
        doc.add(tablaProductos);
        doc.add(new Paragraph("\n"));

        //Retiros de caja
        crearSeccionEncabezado(doc, "Retiros de caja");
        Table tablaRetiros = new Table(UnitValue.createPercentArray(new float[]{2, 3}));
        tablaRetiros.setWidth(UnitValue.createPercentValue(100));
        tablaRetiros.addHeaderCell(celdaHeader("Cantidad Retirada"));
        tablaRetiros.addHeaderCell(celdaHeader("Observaciones"));

        if (datos.getRetiros() != null && !datos.getRetiros().isEmpty()) {
            for (RetiroReporteDTO retiro : datos.getRetiros()) {
                tablaRetiros.addCell(celdaCentro(retiro.getMonto()));
                tablaRetiros.addCell(celdaCentro(retiro.getObservacion()));
            }
        } else {
            tablaRetiros.addCell(new Cell(1, 2).add(new Paragraph("No se registraron retiros de caja."))
                    .setTextAlignment(TextAlignment.CENTER).setPadding(10));
        }

        doc.add(tablaRetiros);
        doc.add(new Paragraph("\n"));

        //Pagos
        crearSeccionEncabezado(doc, "Pagos");
        Table tablaPagos = new Table(UnitValue.createPercentArray(new float[]{2, 4, 2, 3, 2}));
        tablaPagos.setWidth(UnitValue.createPercentValue(100));
        tablaPagos.addHeaderCell(celdaHeader("ID CLIENTE"));
        tablaPagos.addHeaderCell(celdaHeader("NOMBRE CLIENTE"));
        tablaPagos.addHeaderCell(celdaHeader("ID PAGO"));
        tablaPagos.addHeaderCell(celdaHeader("ARTICULO(S)"));
        tablaPagos.addHeaderCell(celdaHeader("TOTAL"));

        if (datos.getPagos() != null && !datos.getPagos().isEmpty()) {
            for (PagoReporteDTO pago : datos.getPagos()) {
                tablaPagos.addCell(celdaCentro(pago.getIdCliente()));
                tablaPagos.addCell(celdaCentro(pago.getNombreCliente()));
                tablaPagos.addCell(celdaCentro(pago.getIdPago()));
                tablaPagos.addCell(celdaCentro(pago.getArticulo()));
                tablaPagos.addCell(celdaCentro(pago.getTotal()));
            }
        } else {
            tablaPagos.addCell(new Cell(1, 5).add(new Paragraph("No se registraron pagos."))
                    .setTextAlignment(TextAlignment.CENTER).setPadding(10));
        }
        doc.add(tablaPagos);
        doc.add(new Paragraph("\n"));

        //Caja
        crearSeccionEncabezado(doc, "Caja");

        CajaReporteDTO caja = datos.getCaja();

        doc.add(new Paragraph("Dinero con el que inició caja").setBold());
        doc.add(new Paragraph(caja.getDineroInicial() + "\n"));

        doc.add(new Paragraph("Dinero con el que debería terminar caja").setBold());
        doc.add(new Paragraph(caja.getDineroDeberiaTerminar() + "\n"));

        doc.add(new Paragraph("Dinero con el que terminó caja").setBold());
        doc.add(new Paragraph(caja.getDineroTermino()));
        doc.close();
    }

    private static void crearSeccionEncabezado(Document doc, String titulo) {
        Paragraph header = new Paragraph(titulo)
                .setBackgroundColor(ColorConstants.BLUE)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setPadding(5)
                .setFontSize(14)
                .setMarginBottom(10);
        doc.add(header);
    }

    private static Cell celdaHeader(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private static Cell celdaCentro(String texto) {
        return new Cell()
                .add(new Paragraph(texto))
                .setTextAlignment(TextAlignment.CENTER);
    }
}