package ui;

import helper.UsuarioAHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.desarollo.entity.Usuarioadministrador;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("usuarioABeanUI")
@ViewScoped
public class UsuarioABeanUI implements Serializable {

    private List<Usuarioadministrador> listaUsuarios;
    private Usuarioadministrador nuevoUsuario;
    private Usuarioadministrador usuarioSeleccionado;

    private String filtroId;
    private String busquedaIdMod;
    private String idEliminar;

    private final UsuarioAHelper helper = new UsuarioAHelper();

    @PostConstruct
    public void init() {
        nuevoUsuario = new Usuarioadministrador();
        usuarioSeleccionado = new Usuarioadministrador();
        cargarUsuarios();
    }

    public void cargarUsuarios() {
        try {
            if (filtroId != null && !filtroId.trim().isEmpty()) {
                Usuarioadministrador u = helper.obtenerUA(filtroId);
                listaUsuarios = new ArrayList<>();
                if (u != null && u.getEstatus() == 1) {
                    listaUsuarios.add(u);
                }
            } else {
                // AQUÍ ES DONDE FINALMENTE SE USA EL MÉTODO listarUA()
                listaUsuarios = helper.listarUA();
            }
            if (listaUsuarios == null) listaUsuarios = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            listaUsuarios = new ArrayList<>();
        }
    }

    public List<Usuarioadministrador> getListaUsuarios() { return listaUsuarios; }
    public Usuarioadministrador getNuevoUsuario() { return nuevoUsuario; }
    public void setNuevoUsuario(Usuarioadministrador nuevoUsuario) { this.nuevoUsuario = nuevoUsuario; }
    public Usuarioadministrador getUsuarioSeleccionado() { return usuarioSeleccionado; }
    public void setUsuarioSeleccionado(Usuarioadministrador usuarioSeleccionado) { this.usuarioSeleccionado = usuarioSeleccionado; }
    public String getFiltroId() { return filtroId; }
    public void setFiltroId(String filtroId) { this.filtroId = filtroId; }
    public String getBusquedaIdMod() { return busquedaIdMod; }
    public void setBusquedaIdMod(String busquedaIdMod) { this.busquedaIdMod = busquedaIdMod; }
    public String getIdEliminar() { return idEliminar; }
    public void setIdEliminar(String idEliminar) { this.idEliminar = idEliminar; }
}