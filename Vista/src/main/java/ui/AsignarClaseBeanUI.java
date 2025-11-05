package ui;

import helper.AsignarClaseHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import java.io.Serializable;

@Named("asignarClaseBeanUI")
@SessionScoped
public class AsignarClaseBeanUI implements Serializable {
    private String idCliente;
    private String idClase;

    private final AsignarClaseHelper helper = new AsignarClaseHelper();

    public void asignarClase() {
        try {
            helper.asignarClaseACliente(idCliente, idClase);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Clase asignada correctamente."));

            PrimeFaces.current().executeScript("PF('dlgAsignarClase').hide();");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    // Getters y setters
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
