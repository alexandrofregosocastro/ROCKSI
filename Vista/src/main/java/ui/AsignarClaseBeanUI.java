package ui;

import helper.AsignarClaseHelper;
import helper.ClienteHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.desarollo.entity.Cliente;
import org.primefaces.PrimeFaces;
import java.io.Serializable;

@Named("asignarClaseBeanUI")
@SessionScoped
public class AsignarClaseBeanUI implements Serializable {
    private String idCliente;
    private String idClase;

    private final AsignarClaseHelper helper = new AsignarClaseHelper();
    private final ClienteHelper clienteHelper = new ClienteHelper();

    public void asignarClase() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (idClase == null || idClase.trim().isEmpty()) {
                throw new Exception("Debe seleccionar una clase antes de continuar.");
            }

            if (idCliente == null || idCliente.trim().isEmpty()) {
                throw new Exception("Debe ingresar un cliente válido antes de continuar.");
            }

            Cliente clienteExistente = clienteHelper.obtenerCliente(idCliente);
            if (clienteExistente == null) {
                throw new Exception("No se encontró ningún cliente con el ID: " + idCliente);
            }

            // Metemos los datos del cliente y clase a la sesión
            fc.getExternalContext().getSessionMap().put("idClase", idClase);
            fc.getExternalContext().getSessionMap().put("idCliente", idCliente);

            // Metemos la bandera que le avisa a la otra página que abra el modal
            fc.getExternalContext().getSessionMap().put("abrirModalPagoClase", true);

            // Cerramos el modal actual y redirigimos a pagos
            PrimeFaces.current().executeScript("PF('dlgAsignarClase').hide();");
            fc.getExternalContext().redirect("pagos.xhtml");

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al asignar clase", e.getMessage()));
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
