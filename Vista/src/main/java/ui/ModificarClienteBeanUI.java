package ui;

import helper.ClienteHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import mx.desarollo.entity.Cliente;
import org.primefaces.PrimeFaces;

@Named("modificarCliBeanUI")
@SessionScoped
public class ModificarClienteBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;

    private Cliente cliente = new Cliente();
    private final ClienteHelper guardarCliente = new ClienteHelper();

    //campos que enlaza el formulario de modificacion
    private String nombre;
    private String apellido;
    private String telefono;
    private String segundoTelefono;
    private String sexo;

    //campo donde el usuario ingresa el ID a buscar
    private String busquedaId;

    //aqui se busca el cliente desde una ventanita en el xhtml
    public void cargarClientePorId() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (busquedaId == null || busquedaId.trim().isEmpty()) {
                fc.addMessage("msgsAsk", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ingrese un ID de cliente válido"));
                PrimeFaces.current().ajax().addCallbackParam("found", false);
                return;
            }

            String id = busquedaId.trim();

            Cliente c = guardarCliente.obtenerCliente(id);

            if (c == null) {
                fc.addMessage("msgsAsk", new FacesMessage(FacesMessage.SEVERITY_ERROR, "No encontrado", "No existe cliente con ID " + id));
                PrimeFaces.current().ajax().addCallbackParam("found", false);
                return;
            }

            // si existe precargar los datos del cliente en la ventana de modificar
            this.cliente = c;

            String nc = c.getNombreCompleto() == null ? "" : c.getNombreCompleto().trim();
            if (nc.isEmpty()) {
                this.nombre = "";
                this.apellido = "";
            } else {
                String[] parts = nc.split("\\s+", 2);
                this.nombre = parts.length > 0 ? parts[0] : "";
                this.apellido = parts.length > 1 ? parts[1] : "";
            }

            this.telefono = c.getTelefono();
            this.segundoTelefono = c.getSegundoTelefono();
            this.sexo = c.getSexo();

            // indicar al cliente que se encontro el cliente
            PrimeFaces.current().ajax().addCallbackParam("found", true);

        } catch (Exception e) {
            fc.addMessage("msgsAsk", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al buscar cliente: " + e.getMessage()));
            PrimeFaces.current().ajax().addCallbackParam("found", false);
        }
    }

    //aqui se modifica al cliente
    public void modificarCliente() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            // asegurar que el cliente fue cargado
            if (this.cliente == null || this.cliente.getIdCliente() == null || this.cliente.getIdCliente().trim().isEmpty()) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay cliente cargado para modificar."));
                return;
            }

            this.cliente.setNombreCompleto(((this.nombre == null) ? "" : this.nombre.trim()) + " " + ((this.apellido == null) ? "" : this.apellido.trim()));
            this.cliente.setTelefono(this.telefono);

            String st = (this.segundoTelefono == null) ? "" : this.segundoTelefono.trim();
            this.cliente.setSegundoTelefono(st.isEmpty() ? null : st);

            String sx = (this.sexo == null) ? "" : this.sexo.trim().toLowerCase();
            this.cliente.setSexo(sx);

            guardarCliente.ModificarCliente(this.cliente);

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificación exitosa", "Cliente modificado correctamente."));

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Modificación inválida", e.getMessage()));
        }
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getSegundoTelefono() { return segundoTelefono; }
    public void setSegundoTelefono(String segundoTelefono) { this.segundoTelefono = segundoTelefono; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getBusquedaId() { return busquedaId; }
    public void setBusquedaId(String busquedaId) { this.busquedaId = busquedaId; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}
