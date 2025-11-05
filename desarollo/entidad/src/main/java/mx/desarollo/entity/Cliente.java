package mx.desarollo.entity;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Cliente")
    private int idCliente;

    @Column(name = "nombreCompleto", nullable = false, length = 100)
    private String nombreCompleto;

    @Column(name = "telefono", nullable = false, unique = true, length = 15)
    private String telefono;

    @Temporal(TemporalType.DATE)
    @Column(name = "fechaRegistro")
    private Date fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Membresia")
    private Membresia idMembresia;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> historialCompras;

    @ManyToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Clase> clases;

    @Column(name = "credito")
    private double credito;

    //constructores
    public Cliente() {}

    public Cliente(String nombreCompleto, String telefono, Date fechaRegistro,
                   Membresia idMembresia, double credito) {
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
        this.idMembresia = idMembresia;
        this.credito = credito;
    }

    //getters y setters
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
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

    public Membresia getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(Membresia idMembresia) {
        this.idMembresia = idMembresia;
    }

    public List<Compra> getHistorialCompras() {
        return historialCompras;
    }

    public void setHistorialCompras(List<Compra> historialCompras) {
        this.historialCompras = historialCompras;
    }

    public List<Clase> getClases() {
        return clases;
    }

    public void setClases(List<Clase> clases) {
        this.clases = clases;
    }

    public double getCredito() {
        return credito;
    }

    public void setCredito(double credito) {
        this.credito = credito;
    }
}