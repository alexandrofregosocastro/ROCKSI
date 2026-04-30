package ui;

import helper.UsuarioRHelper;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.desarollo.entity.Usuariorecepcionista;
import org.primefaces.PrimeFaces;

import java.io.Serializable;

@Named("modificarUsuarioRBeanUI")
@ViewScoped
public class ModificarUsuarioRBeanUI implements Serializable {

    private String idUsuario;
    private Usuariorecepcionista usuarioSeleccionado;
    private final UsuarioRHelper usuarioRHelper = new UsuarioRHelper();

    public void buscarUsuario() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (idUsuario == null || idUsuario.trim().isEmpty()) {
                throw new Exception("Debes ingresar un ID.");
            }

            this.usuarioSeleccionado = usuarioRHelper.obtenerUsuarioR(idUsuario.trim());

            if (this.usuarioSeleccionado == null) {
                throw new Exception("El usuario con ID " + idUsuario + " no existe.");
            }

            if (this.usuarioSeleccionado.getEstatus() == 0) {
                throw new Exception("El recepcionista está actualmente dado de baja..");
            }

            this.idUsuario = null;
            PrimeFaces.current().executeScript("PF('dlgBuscar').hide(); PF('dlgModificar').show();");

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void modificarUsuarioR() {
        try {
            if (usuarioSeleccionado == null) {
                throw new Exception("No hay usuario cargado para modificar.");
            }

            usuarioRHelper.modificarUsuarioR(usuarioSeleccionado);

            addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario modificado correctamente.");
            PrimeFaces.current().executeScript("PF('dlgModificar').hide();");

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al modificar", e.getMessage());
        }
    }

    public void prepararBusqueda() {
        this.idUsuario = null;
        this.usuarioSeleccionado = null;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // getters y setters
    public String getidUsuario() { return idUsuario; }
    public void setidUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public Usuariorecepcionista getUsuarioSeleccionado() { return usuarioSeleccionado; }
    public void setUsuarioSeleccionado(Usuariorecepcionista usuarioSeleccionado) { this.usuarioSeleccionado = usuarioSeleccionado; }
}