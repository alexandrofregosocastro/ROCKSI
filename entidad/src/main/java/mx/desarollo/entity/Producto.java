package mx.desarollo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "producto")
@PrimaryKeyJoinColumn(name = "ID_Producto", referencedColumnName = "ID_Item")
public class Producto extends Item implements Serializable {

    @NotNull
    @Size(max = 45)
    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    @NotNull
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotNull
    @Column(name = "precio", nullable = false)
    private Double precio;

    @NotNull
    @Size(max = 45)
    @Column(name = "proveedor", nullable = false)
    private String proveedor;//Se agrego el campo de proveedor que faltaba antes

    private static int contador = 1000;

    public static void setContador(int nuevoContador) {
        contador = nuevoContador;
    }

    public static String generarNuevoId() {
        return "PR" + contador++;
    }

    public Producto() {
        super();
    }

    public Producto(String id, Double precio, String nombre, Integer stock, String proveedor) {
        super(id);
        this.precio = precio;
        this.nombre = nombre;
        this.stock = stock;
        this.proveedor = proveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getProveedor() { return proveedor; }

    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
}
