package mx.desarollo.entity;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "cliente")
public class Cliente {

    private static int contador = 1000;

    @Id
    @Column(name = "ID_Cliente", length = 45)
    private String idCliente;

    @Column(name = "nombreCompleto", nullable = false, length = 100)
    private String nombreCompleto;

    @Column(name = "telefono", nullable = false, unique = true, length = 15)
    private String telefono;

    @Temporal(TemporalType.DATE)
    @Column(name = "fechaRegistro")
    private Date fechaRegistro;

     /*
     @OneToMany(mappedBy = "cliente", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Pago> historialPagos = new ArrayList<>();
     */

    @ManyToMany
    @JoinTable( //Aqui se hace un join para realizar la tabla puente de estainscrito dentro de la BD
            name = "estainscrito",
            joinColumns = @JoinColumn(name = "ID_Cliente"),
            inverseJoinColumns = @JoinColumn(name = "ID_Clase")
    )
    private List<Clase> clases = new ArrayList<>();

    @Column(name = "credito")
    private double credito;

    @Column(name = "sexo")
    private String sexo;

    @Column(name = "segundoTelefono")
    private String segundoTelefono;

    @Column(name = "cantidadDineroMensual")
    private double cantidadDineroMensual;

    //constructores

    public Cliente() { }


    public Cliente(String nombreCompleto, String telefono, double credito, String sexo, String segundoTelefono) {
        this.idCliente = generarNuevoId();
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.fechaRegistro = new Date();
        this.credito = credito;
        this.sexo = sexo;
        this.segundoTelefono = segundoTelefono;
        this.cantidadDineroMensual = 0;
    }

    // metodo para creacion de ID
    public static synchronized String generarNuevoId() {
        StringBuilder sb = new StringBuilder();
        sb.append("CLI").append(contador++);
        return sb.toString();
    }

    // permite al DAO actualizar el contador
    public static void setContador(int nuevoValor) {
        contador = nuevoValor;
    }

    //getters y setters
    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public double getCredito() {
        return credito;
    }

    public void setCredito(double credito) {
        this.credito = credito;
    }

    @Transient
    public String getApellido() {
        if(nombreCompleto != null && nombreCompleto.contains(" ")) {
            return nombreCompleto.substring(nombreCompleto.indexOf(' ') + 1);
        }
        return "";
    }

    public List<Clase> getClases() {
        return clases;
    }

    public void setClases(List<Clase> clases) {
        this.clases = clases;
    }

    public String getSegundoTelefono() {return segundoTelefono;}

    public void setSegundoTelefono(String segundoTelefono) {this.segundoTelefono = segundoTelefono;}

    public String getSexo() {return sexo;}

    public void setSexo(String sexo) {this.sexo = sexo;}

    public double getCantidadDineroMensual() {return cantidadDineroMensual;}

    public void setCantidadDineroMensual(double cantidadDineroMensual) {this.cantidadDineroMensual = cantidadDineroMensual;}


}