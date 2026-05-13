package ui;

import helper.UsuarioRHelper;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import java.io.Serializable;
import ui.UsuarioRBeanUI;

@Named("bajaUsuarioRBeanUI")
@ViewScoped
public class BajaUsuarioRBeanUI implements Serializable {

    private String idUsuario;

    private final UsuarioRHelper usuarioRHelper = new UsuarioRHelper();

    public void bajaUsuario() {
        try {
            if (idUsuario == null || idUsuario.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID no puede estar vacío.");
                return;
            }

            boolean exito = usuarioRHelper.bajaUsuarioR(this.idUsuario);

            if (exito) {
                UsuarioRBeanUI usuarioRBeanUI = (UsuarioRBeanUI) FacesContext.getCurrentInstance().getApplication()
                        .getELResolver().getValue(FacesContext.getCurrentInstance().getELContext(), null, "usuarioRBeanUI");
                if (usuarioRBeanUI != null) usuarioRBeanUI.cargarUsuarios();
                PrimeFaces.current().ajax().update(":tabUsuarios:formRecep:tablaRecep");

                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario dado de baja correctamente.");
                PrimeFaces.current().executeScript("PF('dlgConfirmEliminar').hide();");
                limpiar();
            } else {
                addMessage(FacesMessage.SEVERITY_WARN, "Aviso", "No se pudo dar de baja al usuario.");
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

    // getters y setters
    public String getidUsuario() { return idUsuario; }
    public void setidUsuario(String idUsuario) { this.idUsuario = idUsuario; }
}