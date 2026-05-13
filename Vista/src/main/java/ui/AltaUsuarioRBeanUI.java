package ui;
import helper.UsuarioRHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.desarollo.entity.Usuariorecepcionista;
import org.primefaces.PrimeFaces;
import ui.UsuarioRBeanUI;

import java.io.Serializable;

//Nombre del Bean
@Named("altaUsuarioRBeanUI")
@SessionScoped
//Clase principal
public class AltaUsuarioRBeanUI implements Serializable {
    private Usuariorecepcionista ur = new Usuariorecepcionista(); // Crea un objeto de tipo usuariorecepcionista
    private UsuarioRHelper guardarUsuarioR = new UsuarioRHelper(); // Crea un objeto de tipo UsuarioRHelper()
    private String nombreCompleto; // String que se llenara con el nombre que se ingresara en la interfaz de usuario
    private String correo; // String que se llenara de acuerdo al correo ingresado en la interfaz de usuario
    private String contraseña; // String que se llenara de acuerdo a lo que la vista obtenga
    private int status = 1; // int de status 1=Alta

    /**
     * Metodo para hacer una alta de un usuario recepcionista que llamara a la instancia de UsuarioRHelper
     * @Throws Si la base de datos rechaza la peticion de la alta o algun dato es null o vacio
     * @return void
     */
    public void altaUsuarioRecepcionista() {
        try {
            this.ur.setIdUsuariorecep(Usuariorecepcionista.generarNuevoId());
            this.ur.setNombreCompleto(this.nombreCompleto);
            this.ur.setCorreo(this.correo);
            this.ur.setContrasena(this.contraseña);
            this.ur.setEstatus(this.status);

            guardarUsuarioR.AltaUsuarioR(ur);

            UsuarioRBeanUI usuarioRBeanUI = (UsuarioRBeanUI) FacesContext.getCurrentInstance().getApplication()
                    .getELResolver().getValue(FacesContext.getCurrentInstance().getELContext(), null, "usuarioRBeanUI");
            if (usuarioRBeanUI != null) usuarioRBeanUI.cargarUsuarios();
            PrimeFaces.current().ajax().update(":tabUsuarios:formRecep:tablaRecep");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Alta realizada con éxito ", "Usuario recepcionista registrado correctamente."));

            PrimeFaces.current().executeScript("PF('dlgAgregarRecep').hide()");
            PrimeFaces.current().ajax().update("formNuevoRecep");

            this.nombreCompleto = "";
            this.correo = "";
            this.contraseña = "";
            this.status = 1;

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alta Inválida", e.getMessage()));
        }
    }

    // Getters y Setters
    public String getNombreCompleto() { return this.nombreCompleto; }
    public void setNombreCompleto(String nombre) { this.nombreCompleto = nombre; }

    public String getCorreo() { return this.correo; }
    public void  setCorreo(String correo) { this.correo = correo; }

    public String getContraseña() {return this.contraseña;}
    public void setContraseña(String contraseña) {this.contraseña = contraseña;}

    public int getStatus() {return status;}
    public void setStatus(int status) {this.status = status;}

}
