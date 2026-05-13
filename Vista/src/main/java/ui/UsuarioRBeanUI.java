package ui;

import helper.UsuarioRHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.desarollo.entity.Usuariorecepcionista;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("usuarioRBeanUI")
@ViewScoped
public class UsuarioRBeanUI implements Serializable {

    private List<Usuariorecepcionista> listaUsuarios;
    private Usuariorecepcionista nuevoUsuario;
    private Usuariorecepcionista usuarioSeleccionado;

    // Variables para los filtros y búsquedas de los modales
    private String filtroId;         // Para la tabla principal
    private String busquedaIdMod;    // Para el modal "Buscar usuario a modificar"
    private String idEliminar;       // Para el modal "Eliminar"

    private final UsuarioRHelper helper = new UsuarioRHelper();

    @PostConstruct
    public void init() {
        nuevoUsuario = new Usuariorecepcionista();
        usuarioSeleccionado = new Usuariorecepcionista();
        cargarUsuarios();
    }

    public void cargarUsuarios() {
        try {
            // Si hay filtro, buscamos por ID, si no, traemos todos
            if (filtroId != null && !filtroId.trim().isEmpty()) {
                Usuariorecepcionista u = helper.obtenerUsuarioR(filtroId);
                listaUsuarios = new ArrayList<>();
                if (u != null && u.getEstatus() == 1) {
                    listaUsuarios.add(u);
                }
            } else {
                listaUsuarios = helper.listarUsuarioR();
            }

            // Evitar nulos para PrimeFaces
            if (listaUsuarios == null) listaUsuarios = new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
            listaUsuarios = new ArrayList<>();
        }
    }

    public List<Usuariorecepcionista> getListaUsuarios() { return listaUsuarios; }
    public Usuariorecepcionista getNuevoUsuario() { return nuevoUsuario; }
    public void setNuevoUsuario(Usuariorecepcionista nuevoUsuario) { this.nuevoUsuario = nuevoUsuario; }
    public Usuariorecepcionista getUsuarioSeleccionado() { return usuarioSeleccionado; }
    public void setUsuarioSeleccionado(Usuariorecepcionista usuarioSeleccionado) { this.usuarioSeleccionado = usuarioSeleccionado; }

    public String getFiltroId() { return filtroId; }
    public void setFiltroId(String filtroId) { this.filtroId = filtroId; }

    public String getBusquedaIdMod() { return busquedaIdMod; }
    public void setBusquedaIdMod(String busquedaIdMod) { this.busquedaIdMod = busquedaIdMod; }

    public String getIdEliminar() { return idEliminar; }
    public void setIdEliminar(String idEliminar) { this.idEliminar = idEliminar; }
}