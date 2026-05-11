package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.util.List;
import mx.desarollo.entity.Cliente;
import helper.ClienteHelper;
import java.io.Serializable;

@Named("clienteBeanUI")
@ViewScoped
public class ClienteBeanUI implements Serializable {
    private List<Cliente> listaClientes; // Lista de clientes necesesaria para consulta de clientes
    private ClienteHelper clienteHelper = new ClienteHelper(); // Instancia del helper de clientes
    private String filtroId; // String para filtrar un cliente por su ID

    /**
     * Metodo para hacer consulta de todos los clientes que llamara a la instancia de clienteHelper
     * @Throws Si la base de datos rechaza la peticion de selec * from tabla
     * @return Una lista de clientes a la List listaClientes
     */
    @PostConstruct
    public void init() {
        try {
            listaClientes = clienteHelper.ObtenerClientes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo para probar la conexion a la base de datos que llamara a la instancia de clienteHelper
     * @Throws Si la base de datos rechaza la peticion de selec * from tabla
     * @return Una lista de clientes a la List listaClientes
     */
    public void probarConexion() {
        try {
            System.out.println("Si se mando a llamar");
            listaClientes = clienteHelper.ObtenerClientes();
            System.out.println("Tabla de clientes actualizada\n Total: " + listaClientes.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo para la busqueda de un cliente por su ID que llamara a la instancia de clienteHelper
     * @Throws Si la base de datos rechaza la peticion del cliente o este no se encuentra
     * @return Un cliente especifico por su ID
     */
    public void filtrarPorId() {
        try {
            // Si el String filtro esta vacio entonces muestra la consulta completa
            if (filtroId == null || filtroId.isEmpty()) {
                listaClientes = clienteHelper.ObtenerClientes();
            } else { // Si no, entonces obtiene el cliente por su ID con la funcion .ObtenerClientesPorID(String IdCliente)
                listaClientes = clienteHelper.ObtenerClientesPorId(filtroId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters y Setters
    public List<Cliente> getListaClientes() {
        return listaClientes;
    }
    public String getFiltroId() {
        return filtroId;
    }
    public void setFiltroId(String filtroId) {
        this.filtroId = filtroId;
    }
}


