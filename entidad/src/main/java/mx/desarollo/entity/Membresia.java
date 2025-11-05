package mx.desarollo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import mx.desarollo.entity.Item;

import java.time.LocalDate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "membresia")
public class Membresia implements Serializable {

    @Id
    @Size(max = 45)
    @Column(name = "ID_Membresia", length = 45)
    private String idMembresia;

    @Temporal(TemporalType.DATE)
    @Column(name = "fechaVencimiento")
    private Date fechaVencimiento;


    /*
    @OneToMany(mappedBy = "membresia", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Cliente> clientes = new ArrayList<>();
    */

    public Membresia() { }

    public Membresia(String idMembresia, Date fechaVencimiento) {
        this.idMembresia = idMembresia;
        this.fechaVencimiento = fechaVencimiento;
    }


    /*
    public void addCliente(Cliente c) {
        if (c != null) {
            clientes.add(c);
        }
    }

    public void removeCliente(Cliente c) {
        if (c != null) {
            clientes.remove(c);
        }
    }
     */

    // Getters y setters
    public String getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(String idMembresia) {
        this.idMembresia = idMembresia;
    }

    /*public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }*/

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /*
    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

     */
}
