package mx.desarollo.integration;

import mx.desarollo.facade.*;

public class ServiceFacadeLocator {

    private static ClienteFacade ClienteFacade;
    private static ClaseFacade ClaseFacade;
    private static ProductoFacade ProductoFacade;
    private static PagaFacade PagaFacade;
    private static UsuarioRFacade UsuarioRFacade;
    private static MembresiaFacade MembresiaFacade;
    private static ReporteFacade reporteFacade;
    private static InventarioDiarioFacade inventarioDiarioFacade;
    private static ReporteMensualFacade reporteMensualFacade;
    private static UsuarioAFacade UsuarioAFacade;
    private static MovimientoCajaFacade MovimientoCajaFacade;


    public static UsuarioAFacade getInstanceAAFacade() {
        if(UsuarioAFacade == null){
            UsuarioAFacade =  new UsuarioAFacade();
            return UsuarioAFacade;
        }
        else{
            return UsuarioAFacade;
        }
    }

    public static ClienteFacade getInstanceClienteFacade() {
        if (ClienteFacade == null) {
            ClienteFacade = new ClienteFacade();
            return ClienteFacade;
        } else {
            return ClienteFacade;
        }
    }
    public static ClaseFacade getInstanceClaseFacade() {
        if(ClaseFacade == null){
            ClaseFacade = new ClaseFacade();
            return ClaseFacade;
        }
        else{
            return ClaseFacade;
        }
    }
    public static ProductoFacade getInstanceProductoFacade() {
        if(ProductoFacade == null){
            ProductoFacade = new ProductoFacade();
            return ProductoFacade;
        }
        else{
            return ProductoFacade;
        }
    }
    public static PagaFacade getInstancePagaFacade() {
        if(PagaFacade == null){
            PagaFacade =  new PagaFacade();
            return PagaFacade;
        }
        else{
            return PagaFacade;
        }
    }
    public static UsuarioRFacade getInstanceURFacade() {
        if(UsuarioRFacade == null){
            UsuarioRFacade =  new UsuarioRFacade();
            return UsuarioRFacade;
        }
        else{
            return UsuarioRFacade;
        }
    }
    public static MembresiaFacade getInstanceMembresiaFacade() {
        if(MembresiaFacade == null){
            MembresiaFacade =  new MembresiaFacade();
            return MembresiaFacade;
        }
        else{
            return MembresiaFacade;
        }
    }

    public static ReporteFacade getInstanceReporteFacade() {
        if (reporteFacade == null) {
            reporteFacade = new ReporteFacade();
        }
        return reporteFacade;
    }

    public static InventarioDiarioFacade getInstanceInventarioDiarioFacade() {
        if (inventarioDiarioFacade == null) {
            inventarioDiarioFacade = new InventarioDiarioFacade();
        }
        return  inventarioDiarioFacade;
    }

    public static ReporteMensualFacade getInstanceReporteMensualFacade() {
        if (reporteMensualFacade == null) {
            reporteMensualFacade = new ReporteMensualFacade();
        }
        return reporteMensualFacade;
    }

    public static MovimientoCajaFacade getInstanceMovimientoCajaFacade() {
        if (MovimientoCajaFacade == null) {
            MovimientoCajaFacade = new MovimientoCajaFacade();
        }
        return MovimientoCajaFacade;
    }

}
