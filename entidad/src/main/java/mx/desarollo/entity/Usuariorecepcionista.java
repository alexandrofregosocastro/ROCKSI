package mx.desarollo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuariorecepcionista")
public class Usuariorecepcionista {
    @Id
    @Size(max = 45)
    @Column(name = "ID_UsuarioRecep", nullable = false, length = 45)
    private String idUsuariorecep;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombreCompleto", nullable = false, length = 100)
    private String nombreCompleto;

    @Size(max = 60)
    @NotNull
    @Column(name = "correo", nullable = false, length = 60)
    private String correo;

    @Size(max = 300)
    @NotNull
    @Column(name = "contrasena", nullable = false, length = 300)
    private String contrasena;

    public String getIdUsuariorecep() {
        return idUsuariorecep;
    }

    public void setIdUsuariorecep(String idUsuariorecep) {
        this.idUsuariorecep = idUsuariorecep;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

}