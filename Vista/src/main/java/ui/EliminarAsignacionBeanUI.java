package ui;

import helper.AsignarClaseHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("eliminarAsignacionBeanUI")
@SessionScoped
public class EliminarAsignacionBeanUI implements Serializable {

    private String idCliente;
    private String idClase;
    private final AsignarClaseHelper helper = new AsignarClaseHelper();

    public void eliminarAsignacion() {
        try {
            if (idCliente == null || idCliente.trim().isEmpty()) {
                throw new Exception("Debe ingresar un ID de cliente.");
            }
            if (idClase == null || idClase.trim().isEmpty()) {
                throw new Exception("Debe seleccionar una clase.");
            }

            helper.eliminarAsignacionClienteClase(idCliente, idClase);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Asignación eliminada",
                            "El cliente fue removido de la clase correctamente."));

            idCliente = null;
            idClase = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdClase() {
        return idClase;
    }

    public void setIdClase(String idClase) {
        this.idClase = idClase;
    }
}
