package mx.desarollo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "reporte")
public class Reporte {
    @Id
    @Size(max = 45)
    @Column(name = "ID_Reporte", nullable = false, length = 45)
    private String idReporte;

    @NotNull
    @Column(name = "fechaGeneracion", nullable = false)
    private LocalDate fechaGeneracion;

    @Size(max = 500)
    @NotNull
    @Column(name = "datos", nullable = false, length = 500)
    private String datos;

    public String getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(String idReporte) {
        this.idReporte = idReporte;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

}