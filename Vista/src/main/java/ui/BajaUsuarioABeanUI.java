package ui;

import helper.UsuarioAHelper;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import java.io.Serializable;
import ui.UsuarioABeanUI;

@Named("bajaUsuarioABeanUI")
@ViewScoped
public class BajaUsuarioABeanUI implements Serializable {

    private String idUsuario;
    private final UsuarioAHelper usuarioAHelper = new UsuarioAHelper();

    // Inyectamos el login para saber quien es el Admin actual
    @Inject
    private LoginBeanUI loginBeanUI;

    public void bajaUsuario() {
        try {
            if (idUsuario == null || idUsuario.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID no puede estar vacío.");
                return;
            }

            // Comparamos el ID ingresado con el ID de la sesion
            if (idUsuario.trim().equalsIgnoreCase(loginBeanUI.getIdUsuario())) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Acción denegada", "No puedes dar de baja tu propio usuario mientras estás en sesión.");
                return; // Salimos del flujo
            }

            boolean exito = usuarioAHelper.bajaUsuarioA(this.idUsuario);

            if (exito) {
                UsuarioABeanUI usuarioABeanUI = (UsuarioABeanUI) FacesContext.getCurrentInstance().getApplication()
                        .getELResolver().getValue(FacesContext.getCurrentInstance().getELContext(), null, "usuarioABeanUI");
                if (usuarioABeanUI != null) usuarioABeanUI.cargarUsuarios();
                PrimeFaces.current().ajax().update(":tabUsuarios:formAdmin:tablaAdmin");

                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Administrador dado de baja correctamente.");
                PrimeFaces.current().executeScript("PF('dlgConfAdmin').hide(); PF('dlgEliminarAdmin').hide();");
                limpiar();
            } else {
                addMessage(FacesMessage.SEVERITY_WARN, "Aviso", "No se pudo dar de baja (revise si existe o ya está inactivo).");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void limpiar() {
        this.idUsuario = "";
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public void prepararEliminacion(String id) {
        this.idUsuario = id;
    }

    // getters y setters
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
}