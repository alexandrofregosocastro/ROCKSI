package ui;

import helper.ClienteHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import mx.desarollo.entity.Cliente;


@Named("homeBeanUI")
@SessionScoped
public class HomeBeanUI implements Serializable {

    private List<Cliente> listaClientes;

    @Inject
    private ClienteHelper clienteHelper;

    @PostConstruct
    public void init() {
        try {
            listaClientes = clienteHelper.ObtenerClientes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Cliente> getListaClientes() {
        return listaClientes;
    }
}
