package mx.desarollo.delegate;

import mx.avanti.desarollo.dao.ClienteDAO;
import mx.avanti.desarollo.integration.ServiceLocator;
import mx.desarollo.entity.Cliente;

import java.util.Date;
import java.util.List;

public class ClienteDelegate {

    private final ClienteDAO clienteDAO;

    public ClienteDelegate() {
        this.clienteDAO = ServiceLocator.getInstanceClienteDAO();
    }

    public void registrarCliente(Cliente cliente) throws Exception {
        if (cliente.getNombreCompleto() == null || cliente.getNombreCompleto().trim().isEmpty()) {
            throw new Exception("El nombre no puede estar vacio.");
        }

        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            throw new Exception("El telefono no puede estar vacio.");
        }

        cliente.setFechaRegistro(new Date());

        clienteDAO.save(cliente);
    }

    /*public Cliente obtenerCliente(int id) {
        return clienteDAO.find(id).orElse(null);
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.findAll();
    }

    public void eliminarCliente(int id) {
        Cliente cliente = clienteDAO.find(id).orElse(null);
        if (cliente != null) {
            clienteDAO.delete(cliente);
        }
    }

    public void actualizarCliente(Cliente cliente) throws Exception {
        if (cliente.getNombreCompleto() == null || cliente.getNombreCompleto().trim().isEmpty()) {
            throw new Exception("El nombre no puede estar vacio.");
        }

        clienteDAO.update(cliente);
    }*/
}