package ui;
import helper.UsuarioAHelper;
import helper.UsuarioRHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.desarollo.entity.Cliente;
import mx.desarollo.entity.Usuarioadministrador;
import mx.desarollo.entity.Usuariorecepcionista;
import org.primefaces.PrimeFaces;
import ui.UsuarioABeanUI;

import java.io.IOException;
import java.io.Serializable;

//Nombre del Bean
@Named("altaUsuarioABeanUI")
@SessionScoped
//Clase principal
public class AltaUsuarioABeanUI implements Serializable {
    private Usuarioadministrador ua = new Usuarioadministrador(); // Crea un objeto de tipo usuarioadministrador
    private UsuarioAHelper guardarUsuarioA = new UsuarioAHelper(); // Crea un objeto de tipo UsuarioAHelper()
    private String nombreCompleto; // String que se llenara con el nombre que se ingresara en la interfaz de usuario
    private String correo; // String que se llenara de acuerdo al correo ingresado en la interfaz de usuario
    private String contraseña; // String que se llenara de acuerdo a lo que la vista obtenga
    private int status = 1; // int de status 1=Alta

    /**
     * Metodo para hacer una alta de un usuario administrador que llamara a la instancia de UsuarioAHelper
     * @Throws Si la base de datos rechaza la peticion de la alta o algun dato es null o vacio
     * @return void
     */
    public void altaUsuarioAdministrador() {
        try {
            this.ua.setIdUsuarioadmin(Usuarioadministrador.generarNuevoId());
            this.ua.setNombreCompleto(this.nombreCompleto);
            this.ua.setCorreo(this.correo);
            this.ua.setContrasena(this.contraseña);
            this.ua.setEstatus(this.status);

            guardarUsuarioA.AltaUsuarioA(this.ua);

            UsuarioABeanUI usuarioABeanUI = (UsuarioABeanUI) FacesContext.getCurrentInstance().getApplication()
                    .getELResolver().getValue(FacesContext.getCurrentInstance().getELContext(), null, "usuarioABeanUI");
            if (usuarioABeanUI != null) usuarioABeanUI.cargarUsuarios();
            PrimeFaces.current().ajax().update(":tabUsuarios:formAdmin:tablaAdmin");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Alta realizada con éxito ", "Usuario administrador registrado correctamente."));

            PrimeFaces.current().executeScript("PF('dlgAgregarAdmin').hide()");
            PrimeFaces.current().ajax().update("formNuevoAdmin");

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
