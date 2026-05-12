package ui;

import helper.ClienteHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.Serializable;

@Named("bajaClienteBeanUI")
@SessionScoped
public class BajaClienteBeanUI implements Serializable {

    private String idCliente;
    private final ClienteHelper clienteHelper = new ClienteHelper();

    public void prepararEliminacion(String id) {
        this.idCliente = id;
    }

    public void eliminarCliente() {
        try {
            boolean eliminado = clienteHelper.eliminarCliente(idCliente);
            //Se usa un boolean para confirmar si la transaccion fue un exito o no

            if (eliminado) {
                FacesContext fc = FacesContext.getCurrentInstance();
                ClienteBeanUI clienteBeanUI = (ClienteBeanUI) fc.getApplication().getELResolver().getValue(fc.getELContext(), null, "clienteBeanUI");

                if (clienteBeanUI != null) {
                    clienteBeanUI.probarConexion();
                }

                fc.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Baja Exitosa", "Cliente eliminado correctamente."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "No encontrado", "No existe ningún cliente con ese ID."));
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar", e.getMessage()));
        }
    }



    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }
}
