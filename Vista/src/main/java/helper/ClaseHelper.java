package helper;
import java.io.Serializable;

import mx.desarollo.entity.Clase;
import mx.desarollo.entity.Cliente;
import mx.desarollo.integration.ServiceFacadeLocator;

import java.io.Serializable;
import java.util.List;

public class ClaseHelper implements Serializable {
    public void AltaClase(Clase cla) throws Exception {
        try {
            ServiceFacadeLocator.getInstanceClaseFacade().registrarClase(cla);
        } catch (Exception e) {
            throw new Exception("Error al registrar la clase: " + e.getMessage());
        }
    }

    public boolean eliminarClase(String idClase) throws Exception {
        return ServiceFacadeLocator.getInstanceClaseFacade().eliminarClase(idClase);
    }

    /**
     * Metodo para hacer consulta de todos los clientes que llamara a la instancia de ClienteFacade
     * @Throws Si la base de datos rechaza la peticion de selec * from tabla
     * @Param Objeto de tipo Cliente
     * @return Una lista de clientes
     */
    public void ModificarClase(Clase cla) throws Exception {
        ServiceFacadeLocator.getInstanceClaseFacade().actualizarClase(cla);
    }


    public Clase obtenerClase(String id) {
        return ServiceFacadeLocator.getInstanceClaseFacade().obtenerClasePorId(id);

    }

    public List<Clase> listarClases() {
        return ServiceFacadeLocator.getInstanceClaseFacade().listarClases();
    }

}
