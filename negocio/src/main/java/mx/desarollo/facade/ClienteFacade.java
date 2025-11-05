package mx.desarollo.facade;

import mx.desarollo.delegate.ClienteDelegate;
import mx.desarollo.entity.Cliente;
import java.util.List;

public class ClienteFacade {

    private ClienteDelegate clienteDelegate = new ClienteDelegate();

    public void registrarCliente(Cliente cliente) throws Exception {
        clienteDelegate.registrarCliente(cliente);
    }

    /**
     * Metodo para hacer busqueda por ID en los clientes, llamara a la instancia de ClienteDelegate
     * @Throws Si la base de datos rechaza la peticion de busqueda por ID
     * @Params Objeto de tipo String id
     * @return Una lista con los clientes que cumplen con id del cliente especificado
     */
    public List<Cliente> obtenerCliente(String id) {
        return clienteDelegate.obtenerClientePorId(id);

    }



    public Cliente obtenerClientePorId(String id) {
        return clienteDelegate.obtenerCliente(id);
    }

    /**
     * Metodo para hacer consulta de todos los clientes que llamara a la instancia de ClienteDelegate
     * @Throws Si la base de datos rechaza la peticion de selec * from tabla
     * @return Una lista de clientes que contendra todos los clientes de la base de datos
     */
    public List<Cliente> listarClientes() {
        return clienteDelegate.listarClientes();
    }

    public boolean eliminarCliente(String idCliente) throws Exception {
        return clienteDelegate.eliminarCliente(idCliente);
    }

    public void actualizarCliente(Cliente cliente) throws Exception {
        clienteDelegate.actualizarCliente(cliente);
    }
}
