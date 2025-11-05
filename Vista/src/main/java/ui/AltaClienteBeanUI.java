package ui;
import helper.ClienteHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.desarollo.entity.Cliente;

import java.io.Serializable;

//Nombre del Bean
@Named("altaCliBeanUI")
@SessionScoped
//Clase principal
public class AltaClienteBeanUI implements Serializable {
    private Cliente cliente = new Cliente(); //Crea un objeto de tipo cliente
    private ClienteHelper guardarCliente = new ClienteHelper(); //Crea un objeto de tipo ClienteHelper
    private String nombre; //String que se llenara de acuerdo a lo que la vista obtenga
    private String apellido; //String que se llenara de acuerdo a lo que la vista obtenga
    private String telefono; //String que se llenara de acuerdo a lo que la vista obtenga
    private String segundoTelefono; //String que se llenara de acuerdo a lo que la vista obtenga
    private String sexo;
    private double cantidadDineroMensual; //String que se llenara de acuerdo a lo que la vista obtenga

    //Se llama a este metodo para crear el objeto de cliente y mandarselo a las otras capas
    public void altaCliente() {
        try {
            this.cliente.setIdCliente(Cliente.generarNuevoId());
            this.cliente.setNombreCompleto(this.nombre + " " + this.apellido);
            this.cliente.setTelefono(this.telefono);
            this.cliente.setSexo(this.sexo);
            this.cliente.setSegundoTelefono(this.segundoTelefono);
            this.cliente.setCantidadDineroMensual(0);
            this.guardarCliente.AltaCliente(this.cliente);
            FacesContext.getCurrentInstance().addMessage((String)null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Alta Exitosa", "Cliente creado..."));
            this.cliente = new Cliente();
            this.nombre = "";
            this.apellido = "";
            this.telefono = "";
            this.sexo = "";
            this.cantidadDineroMensual = 0;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage((String)null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alta Invalida", e.getMessage()));
        }

    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public String getApellido() {
        return this.apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getSegundoTelefono() {return segundoTelefono;}

    public void setSegundoTelefono(String telefonoOpcional) {this.segundoTelefono = telefonoOpcional;}

    public String getSexo() {return sexo;}

    public void setSexo(String sexo) {this.sexo = sexo;}

    public double getCantidadDineroMensual() {return cantidadDineroMensual;}

    public void setCantidadDineroMensual(double cantidadDineroMensual) {this.cantidadDineroMensual = cantidadDineroMensual;}



}
