package mx.desarollo.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clase")
@PrimaryKeyJoinColumn(name = "ID_Clase") //Aqui se vincula el id con el id de item
public class Clase extends Item implements Serializable {

    //Contador utilizado para la generacion de ids
    private static int contador = 1000;

    //Esto se utiliza para obtener el ID creado en el dao (solo el numero)
    public static void setContador(int valor) {
        contador = valor;
    }

    //Aqui se genera el id
    public static synchronized String generarNuevoId() {
        return "CLA" + (contador++);
    }

    @Column(name = "nombre", length = 45, nullable = false)
    private String nombre;

    @Column(name = "horario", length = 45, nullable = false)
    private String horario;

    @Column(name = "cupoMaximo", nullable = false)
    private int cupoMaximo;

    @Column(name = "maestro", length = 100, nullable = false)
    private String maestro;

    @ManyToMany(mappedBy = "clases")
    private List<Cliente> clientes = new ArrayList<>();

    @Column(name = "diasImpartida", length = 100, nullable = false)
    private String dias;

    public Clase() {
    }

    public Clase(String idClase, String nombre, String horario, int cupoMaximo, String maestro, String diasImpartidos) {
        super(idClase);
        this.nombre = nombre;
        this.horario = horario;
        this.cupoMaximo = cupoMaximo;
        this.maestro = maestro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public String getMaestro() {
        return maestro;
    }

    public void setMaestro(String maestro) {
        this.maestro = maestro;
    }

    @Transient
    public String getIdClase() {
        return super.getIdItem();
    }

    public void setIdClase(String idClase) {
        super.setIdItem(idClase);
    }

    public List<Cliente> getClientes() {return clientes;}

    public void setClientes(List<Cliente> clientes) {this.clientes = clientes;}


    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }
}
