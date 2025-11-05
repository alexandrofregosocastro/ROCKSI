package mx.desarollo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "cortecaja")
public class Cortecaja {
    @Id
    @Size(max = 45)
    @Column(name = "ID_Corte", nullable = false, length = 45)
    private String idCorte;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull
    @Column(name = "totalDiario", nullable = false)
    private Double totalDiario;

    public String getIdCorte() {
        return idCorte;
    }

    public void setIdCorte(String idCorte) {
        this.idCorte = idCorte;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Double getTotalDiario() {
        return totalDiario;
    }

    public void setTotalDiario(Double totalDiario) {
        this.totalDiario = totalDiario;
    }

}