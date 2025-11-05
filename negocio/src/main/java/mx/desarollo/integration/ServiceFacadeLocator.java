package mx.desarollo.integration;

import mx.desarollo.facade.ClienteFacade;
import mx.desarollo.facade.FacadeUsuario;

public class ServiceFacadeLocator {

    private static ClienteFacade ClienteFacade;
    private static FacadeUsuario facadeUsuario;

    public static ClienteFacade getInstanceClienteFacade() {
        if (ClienteFacade == null) {
            ClienteFacade = new ClienteFacade();
            return ClienteFacade;
        } else {
            return ClienteFacade;
        }
    }

    public static FacadeUsuario getInstanceFacadeUsuario() {
        if (facadeUsuario == null) {
            facadeUsuario = new FacadeUsuario();
            return facadeUsuario;
        } else {
            return facadeUsuario;
        }
    }
}
