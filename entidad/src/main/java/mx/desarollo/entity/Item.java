package mx.desarollo.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "item")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Item implements Serializable {

    @Id
    @Column(name = "ID_Item", length = 45, nullable = false)
    private String idItem;

    @Column(name = "ID_UsuarioAdmin", length = 45, nullable = false)
    private String idUsuarioAdmin;

    @Column(name = "tipo", length = 15, nullable = false)
    private String tipo = "";

    public Item() {
    }

    public Item(String idItem) {
        this.idItem = idItem;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public String getIdUsuarioAdmin() {
        return idUsuarioAdmin;
    }

    public void setIdUsuarioAdmin(String idUsuarioAdmin) {
        this.idUsuarioAdmin = idUsuarioAdmin;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
