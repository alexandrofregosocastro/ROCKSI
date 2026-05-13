package ui;

import helper.UsuarioAHelper;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.desarollo.entity.Usuarioadministrador;
import org.primefaces.PrimeFaces;
import java.io.Serializable;
import ui.UsuarioABeanUI;

@Named("modificarUsuarioABeanUI")
@ViewScoped
public class ModificarUsuarioABeanUI implements Serializable {

    private String idUsuario;
    private Usuarioadministrador usuarioSeleccionado;
    private final UsuarioAHelper usuarioAHelper = new UsuarioAHelper();

    // buscar usuario para cargarlo en el dialogo
    public void buscarUsuario() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (idUsuario == null || idUsuario.trim().isEmpty()) {
                throw new Exception("Debes ingresar un ID para buscar.");
            }

            this.usuarioSeleccionado = usuarioAHelper.obtenerUA(idUsuario.trim());

            if (this.usuarioSeleccionado == null) {
                throw new Exception("El administrador con ID " + idUsuario + " no existe.");
            }

            if (this.usuarioSeleccionado.getEstatus() == 0) {
                throw new Exception("El administrador está actualmente dado de baja..");
            }

            this.idUsuario = null;
            PrimeFaces.current().executeScript("PF('dlgBuscarAdmin').hide(); PF('dlgModificarAdmin').show();");

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void modificarUsuarioA() {
        try {
            if (usuarioSeleccionado == null) {
                throw new Exception("No hay usuario cargado para modificar.");
            }

            usuarioAHelper.modificarUsuarioA(usuarioSeleccionado);

            UsuarioABeanUI usuarioABeanUI = (UsuarioABeanUI) FacesContext.getCurrentInstance().getApplication()
                    .getELResolver().getValue(FacesContext.getCurrentInstance().getELContext(), null, "usuarioABeanUI");
            if (usuarioABeanUI != null) usuarioABeanUI.cargarUsuarios();
            PrimeFaces.current().ajax().update(":tabUsuarios:formAdmin:tablaAdmin");

            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Administrador modificado correctamente.");
            PrimeFaces.current().executeScript("PF('dlgModificarAdmin').hide();");

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error al modificar", e.getMessage());
        }
    }

    public void prepararBusqueda() {
        this.idUsuario = null;
        this.usuarioSeleccionado = null;
    }

    // Metodo para mostrar mensajes
    private void mostrarMensaje(FacesMessage.Severity severidad, String titulo, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, titulo, detalle));
    }

    // getters y setters
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public Usuarioadministrador getUsuarioSeleccionado() { return usuarioSeleccionado; }
    public void setUsuarioSeleccionado(Usuarioadministrador usuarioSeleccionado) { this.usuarioSeleccionado = usuarioSeleccionado; }
}