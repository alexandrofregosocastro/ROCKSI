package ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.util.List;
import mx.desarollo.entity.Cliente;
import helper.ClienteHelper;
import java.io.Serializable;

@Named("clienteBeanUI")
@SessionScoped
public class ClienteBeanUI implements Serializable {
    private List<Cliente> listaClientes;
    private ClienteHelper clienteHelper = new ClienteHelper();
    private String filtroId;

    @PostConstruct
    public void init() {
        try {
            listaClientes = clienteHelper.ObtenerClientes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void probarConexion() {
        try {
            System.out.println("Si se mando a llamar");
            listaClientes = clienteHelper.ObtenerClientes();
            System.out.println("Tabla de clientes actualizada\n Total: " + listaClientes.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void filtrarPorId() {
        try {
            if (filtroId == null || filtroId.isEmpty()) {
                listaClientes = clienteHelper.ObtenerClientes();
            } else {
                listaClientes = clienteHelper.ObtenerClientesPorId(filtroId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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


