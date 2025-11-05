package mx.desarollo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EstainscritoId implements Serializable {
    private static final long serialVersionUID = 7732184390680520565L;
    @Size(max = 45)
    @NotNull
    @Column(name = "ID_Cliente", nullable = false, length = 45)
    private String idCliente;

    @Size(max = 45)
    @NotNull
    @Column(name = "ID_Clase", nullable = false, length = 45)
    private String idClase;

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdClase() {
        return idClase;
    }

    public void setIdClase(String idClase) {
        this.idClase = idClase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EstainscritoId entity = (EstainscritoId) o;
        return Objects.equals(this.idCliente, entity.idCliente) &&
                Objects.equals(this.idClase, entity.idClase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCliente, idClase);
    }

}