package mx.desarollo.delegate;

import jakarta.persistence.EntityManager;
import mx.avanti.desarollo.dao.InventarioDiarioDAO;
import mx.avanti.desarollo.dao.PagaDAO;
import mx.avanti.desarollo.dao.ProductoDAO;
import mx.avanti.desarollo.persistence.HibernateUtil;
import mx.desarollo.entity.*;
import mx.desarollo.dto.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporteDelegate {

    public ReporteDelegate() {}

    private static final double DINERO_INICIAL_CAJA = 0;//Esto se va a cambiar cuando quede lo de abrir caja
    //Metodo principal que recolecta, procesa y empaqueta toda la informacion necesaria para el reporte diario.
    public ReporteDiarioDTO generarDatosReporteDiario(LocalDate fecha) {

        EntityManager em = HibernateUtil.getEntityManager();

        try {
            //Instanciar todos los daos que necesitamos
            PagaDAO pagaDAO = new PagaDAO(em);
            ProductoDAO productoDAO = new ProductoDAO(em);
            InventarioDiarioDAO inventarioDAO = new InventarioDiarioDAO(em);

            //Procesar los pagos
            List<Paga> pagosDelDia = pagaDAO.findByFecha(fecha);
            List<PagoReporteDTO> pagosDTO = procesarPagos(pagosDelDia);
            List<RetiroReporteDTO> retirosDTO = new ArrayList<>();

            //Procesar los productos
            List<Producto> todosLosProductos = productoDAO.findAll();
            List<InventarioDiario> snapshots = inventarioDAO.findByFecha(fecha);
            List<ProductoReporteDTO> productosDTO = procesarProductos(todosLosProductos, snapshots, pagosDelDia);
            double totalMontoCaja = 0.0;

            for (Paga p : pagosDelDia) {
                //Se suma todo el monto de pagos, los retiros son negativos asi que se calcula eso tmbn solo
                totalMontoCaja += p.getMonto();

                String idItem = p.getIdItem().getIdItem();

                //Si el id de item es retiro de caja, se agrega en su dicha seccion
                if ("RC1000".equals(idItem)) {
                    retirosDTO.add(new RetiroReporteDTO(p.getMonto(), "Retiro de Caja"));
                } else {
                    pagosDTO.add(crearPagoDTO(p));
                }
            }

            double dineroDeberiaTerminar = DINERO_INICIAL_CAJA + totalMontoCaja;

            CajaReporteDTO cajaDTO = new CajaReporteDTO(
                    DINERO_INICIAL_CAJA,
                    dineroDeberiaTerminar,
                    dineroDeberiaTerminar//Aqui faltaria que el usuario ingrese el dinero con el que se termino
            );

            return new ReporteDiarioDTO(fecha, productosDTO, pagosDTO, retirosDTO, cajaDTO);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar los datos del reporte: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }


    //Convierte la lista de entidades de Paga a una lista de dtos para el reporte
    private List<PagoReporteDTO> procesarPagos(List<Paga> pagos) {
        List<PagoReporteDTO> dtos = new ArrayList<>();

        for (Paga p : pagos) {
            String idCliente = p.getIdCliente().getIdCliente();
            String nombreCliente = p.getIdCliente().getNombreCompleto(); // Sigo asumiendo que Cliente.java tiene getNombreCompleto()
            String idPago = p.getIdPaga();
            double total = p.getMonto();

            //Variable para guardar el nombre del artículo
            String articulo = "";
            Item item = p.getIdItem();

            //Aqui se comprueba de que tipo es el item
            if (item instanceof Producto) {
                //Si es Producto lo convertimos a Producto y usamos getNombre()
                articulo = ((Producto) item).getNombre();

            } else if (item instanceof Clase) {
                //Si es Clase lo convertimos a Clase y usamos getNombre()
                articulo = ((Clase) item).getNombre();

            } else if (item instanceof Membresia) {
                // Membresia no tiene nombre asi que le asignamos uno
                articulo = "Membresía";

            }
            dtos.add(new PagoReporteDTO(idCliente, nombreCliente, idPago, articulo, total));
        }
        return dtos;
    }

    private List<ProductoReporteDTO> procesarProductos(List<Producto> productos, List<InventarioDiario> snapshots, List<Paga> pagos) {

        Map<String, Integer> mapaStockInicial = new HashMap<>();
        for (InventarioDiario snapshot : snapshots) {
            mapaStockInicial.put(snapshot.getIdProducto(), snapshot.getStockInicial());
        }

        Map<String, Integer> mapaVentas = new HashMap<>();
        for (Paga pago : pagos) {
            // Solo procesamos pagos que son de Productos
            if (pago.getIdItem() instanceof Producto) {
                Producto p = (Producto) pago.getIdItem();
                String idProducto = p.getIdItem();
                //Aqui se calculan los articulos vendidos dividiendo el precio final por el precio del articulo
                int cantidadVendida = 0;
                if (p.getPrecio() != null && p.getPrecio() > 0) {
                    cantidadVendida = (int) (pago.getMonto() / p.getPrecio());
                }
                //Acumulamos por si se vendio el mismo producto en pagos diferentes
                mapaVentas.put(idProducto, mapaVentas.getOrDefault(idProducto, 0) + cantidadVendida);
            }
        }

        List<ProductoReporteDTO> dtos = new ArrayList<>();
        for (Producto p : productos) {
            String id = p.getIdItem();
            int inicial = mapaStockInicial.getOrDefault(id, 0);
            int vendida = mapaVentas.getOrDefault(id, 0);
            int fin = p.getStock(); //Stock final
            dtos.add(new ProductoReporteDTO(p.getNombre(), inicial, vendida, fin));
        }
        return dtos;
    }

    //Este metodo se encarga de generar un PagoReporteDTO y asignarle el tipo
    private PagoReporteDTO crearPagoDTO(Paga p) {
        String idCliente = p.getIdCliente().getIdCliente();
        String nombreCliente = p.getIdCliente().getNombreCompleto();
        String idPago = p.getIdPaga();
        double total = p.getMonto();
        String articulo;

        Item item = p.getIdItem();

        if (item instanceof Producto) {
            articulo = ((Producto) item).getNombre();
        } else if (item instanceof Clase) {
            articulo = ((Clase) item).getNombre();
        } else if (item instanceof Membresia) {
            articulo = "Membresia";
        } else {
            if ("PAD1000".equals(item.getIdItem())) {
                articulo = "Pago Adelantado (Credito)";
            } else {
                articulo = "";
            }
        }

        return new PagoReporteDTO(idCliente, nombreCliente, idPago, articulo, total);
    }
}