package helper;

import mx.desarollo.entity.Usuariorecepcionista;
import mx.desarollo.integration.ServiceFacadeLocator;

import java.io.Serializable;
import java.util.List;

public class UsuarioRHelper implements Serializable {

    /**
     * Metodo para hacer alta de un usuario recepcionista que llamara a la instancia de UsuarioRFacade
     * @Throws Si la base de datos rechaza el registro
     * @Param Objeto de tipo Usuariorecepcionista
     */
    public void AltaUsuarioR(Usuariorecepcionista ur) throws Exception {
        ServiceFacadeLocator.getInstanceURFacade().registrarUsuarioRecepcionista(ur);
    }

    /**
     * Metodo para hacer consulta de todos los clientes que llamara a la instancia de ClienteFacade
     * @Throws Si la base de datos rechaza la peticion de selec * from tabla
     * @Param Objeto de tipo Cliente
     * @return Una lista de clientes
     */
    public Usuariorecepcionista obtenerUsuarioR(String id) {
        return ServiceFacadeLocator.getInstanceURFacade().obtenerUsuarioRP(id);

    }

    public List<Usuariorecepcionista> listarUsuarioR() {
        return ServiceFacadeLocator.getInstanceURFacade().listarUsuarioR();
    }

    public void modificarUsuarioR(Usuariorecepcionista id) throws Exception {
        ServiceFacadeLocator.getInstanceURFacade().modificarUsuarioR(id);
    }

    public boolean bajaUsuarioR(String id) throws Exception {
        return ServiceFacadeLocator.getInstanceURFacade().bajaUsuarioR(id);
    }

}
