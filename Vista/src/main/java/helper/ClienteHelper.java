package helper;

import java.io.Serializable;
import java.util.List;

import mx.desarollo.entity.Cliente;
import mx.desarollo.integration.ServiceFacadeLocator;

    /**
     * Metodo para hacer alta de un cliente que llamara a la instancia de ClienteFacade
     * @Throws Si la base de datos rechaza el registro
     * @Param Objeto de tipo Cliente
     */
    public class ClienteHelper implements Serializable {
    public void AltaCliente(Cliente cli) throws Exception {
        ServiceFacadeLocator.getInstanceClienteFacade().registrarCliente(cli);
    }
        public boolean eliminarCliente(String idCliente) throws Exception {
            return ServiceFacadeLocator.getInstanceClienteFacade().eliminarCliente(idCliente);
        }
    /**
     * Metodo para hacer consulta de todos los clientes que llamara a la instancia de ClienteFacade
     * @Throws Si la base de datos rechaza la peticion de selec * from tabla
     * @Param Objeto de tipo Cliente
     * @return Una lista de clientes
     */
    public List<Cliente> ObtenerClientes() throws Exception {
        return ServiceFacadeLocator.getInstanceClienteFacade().listarClientes();
    }
        /**
         * Metodo para hacer busqueda por ID en los clientes, que llamara a la instancia de ClienteFacade
         * @Throws Si la base de datos rechaza la peticion de busqueda por ID
         * @Param Objeto de tipo String id
         * @return Una lista con los clientes que cumplen con id del cliente especificado
         */
    public List<Cliente> ObtenerClientesPorId(String id) throws Exception {
            return ServiceFacadeLocator.getInstanceClienteFacade().obtenerCliente(id);
    }

    public void ModificarCliente(Cliente cli) throws Exception {
        ServiceFacadeLocator.getInstanceClienteFacade().actualizarCliente(cli);
    }

    public Cliente obtenerCliente(String id) {
        return ServiceFacadeLocator.getInstanceClienteFacade().obtenerClientePorId(id);
    }
    }

