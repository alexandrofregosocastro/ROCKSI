package mx.desarollo.integration;

import mx.desarollo.facade.ClienteFacade;

public class ServiceFacadeLocator {

    private static ClienteFacade ClienteFacade;

    public static ClienteFacade getInstanceClienteFacade() {
        if (ClienteFacade == null) {
            ClienteFacade = new ClienteFacade();
            return ClienteFacade;
        } else {
            return ClienteFacade;
        }
    }
}
