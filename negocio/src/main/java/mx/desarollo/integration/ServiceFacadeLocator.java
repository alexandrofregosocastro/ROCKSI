package mx.desarollo.integration;

import mx.desarollo.facade.ClaseFacade;
import mx.desarollo.facade.ClienteFacade;
import mx.desarollo.facade.ProductoFacade;

public class ServiceFacadeLocator {

    private static ClienteFacade ClienteFacade;
    private static ClaseFacade ClaseFacade;
    private static ProductoFacade ProductoFacade;

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

}
