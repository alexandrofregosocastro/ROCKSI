package ui;

import helper.ClaseHelper;
import helper.ClienteHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import mx.desarollo.entity.Clase;
import mx.desarollo.entity.Cliente;
import org.primefaces.PrimeFaces;

@Named("modificarClaBeanUI")
@SessionScoped
public class ModificarClaseBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;

    private Clase clase = new Clase();
    private final ClaseHelper guardarClase = new ClaseHelper();

    //campos que enlaza el formulario de modificacion
    private String nombre;
    private String horaInicio;
    private String horaFinal;
    private int cupoMaximo;
    private String maestro;
    private String diasImpartidos;
    private Date horaInicioDate;
    private Date horaFinalDate;


    //campo donde el usuario ingresa el ID a buscar
    private String busquedaId;

    //aqui se busca la clase desde una ventanita en el xhtml
    public void cargarClasePorId() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (busquedaId == null || busquedaId.trim().isEmpty()) {
                fc.addMessage("msgsAsk", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ingrese un ID de clase válido"));
                PrimeFaces.current().ajax().addCallbackParam("found", false);
                return;
            }

            String id = busquedaId.trim();
            Clase cla = guardarClase.obtenerClase(id);

            if (cla == null) {
                fc.addMessage("msgsAsk", new FacesMessage(FacesMessage.SEVERITY_ERROR, "No encontrado", "No existe clase con ID " + id));
                PrimeFaces.current().ajax().addCallbackParam("found", false);
                return;
            }

            // si existe precargar los datos de la clase en la ventana de modificar
            this.clase = cla;
            this.nombre = cla.getNombre();
            this.cupoMaximo = cla.getCupoMaximo();
            this.maestro = cla.getMaestro();
            this.diasImpartidos = cla.getDias();

            String horario = cla.getHorario();

            if (horario != null && horario.contains("a")) {
                String[] partes = horario.split("a");

                String horaInicio = partes[0].trim();
                String horaFinal = partes[1].trim();
                if (horaInicio.length() == 4) horaInicio = "0" + horaInicio;
                if (horaFinal.length() == 4) horaFinal = "0" + horaFinal;

                this.horaInicio = horaInicio;
                this.horaFinal = horaFinal;
            } else if (horario != null && horario.contains("-")) {
                String[] partes = horario.split("-");

                String horaInicio = partes[0].trim();
                String horaFinal = partes[1].trim();
                if (horaInicio.length() == 4) horaInicio = "0" + horaInicio;
                if (horaFinal.length() == 4) horaFinal = "0" + horaFinal;

                this.horaInicio = horaInicio;
                this.horaFinal = horaFinal;
            }

            PrimeFaces.current().ajax().addCallbackParam("found", true);

        } catch (Exception e) {
            fc.addMessage("msgsAsk", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al buscar clase: " + e.getMessage()));
            PrimeFaces.current().ajax().addCallbackParam("found", false);
        }
    }

    public Date getHoraInicioDate() {
        try {
            return (horaInicio != null) ? new SimpleDateFormat("HH:mm").parse(horaInicio) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public void setHoraInicioDate(Date horaInicioDate) {
        this.horaInicioDate = horaInicioDate;
        if (horaInicioDate != null) {
            this.horaInicio = new SimpleDateFormat("HH:mm").format(horaInicioDate);
        }
    }

    public Date getHoraFinalDate() {
        try {
            return (horaFinal != null) ? new SimpleDateFormat("HH:mm").parse(horaFinal) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public void setHoraFinalDate(Date horaFinalDate) {
        this.horaFinalDate = horaFinalDate;
        if (horaFinalDate != null) {
            this.horaFinal = new SimpleDateFormat("HH:mm").format(horaFinalDate);
        }
    }

    //aqui se modifica a la clase
    public void modificarClase() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            // asegurar que el cliente fue cargado
            if (this.clase == null || this.clase.getIdClase() == null || this.getClase().getIdClase().trim().isEmpty()) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay clase cargada para modificar."));
                return;
            }

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime inicio = LocalTime.parse(horaInicio, formato);
            LocalTime fin = LocalTime.parse(horaFinal, formato);

            if (!inicio.isBefore(fin)) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error", "La hora de inicio no debe ser mayor o igual que la hora final."));
                return;
            }

            this.clase.setNombre(((this.nombre == null) ? "" : this.nombre.trim()));
            this.clase.setHorario(horaInicio + " a " + horaFinal);
            this.clase.setCupoMaximo(cupoMaximo);
            this.clase.setMaestro(((this.maestro == null) ? "" : this.maestro.trim()));
            this.clase.setDias(((this.diasImpartidos == null) ? "" : this.diasImpartidos.trim()));

            guardarClase.ModificarClase(this.clase);

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificación exitosa", "Clase modificada correctamente."));

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Modificación inválida", e.getMessage()));
        }
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFinal() { return horaFinal; }
    public void setHoraFinal(String horaFinal) { this.horaFinal = horaFinal; }

    public String getMaestro() { return maestro; }
    public void setMaestro(String maestro) { this.maestro = maestro; }

    public Integer  getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(Integer cupoMaximo) { this.cupoMaximo = cupoMaximo; }

    public Clase  getClase() { return clase; }
    public void setClase(Clase clase) { this.clase = clase; }

    public String getBusquedaId() { return busquedaId; }
    public void setBusquedaId(String busquedaId) { this.busquedaId = busquedaId; }

    public String getDiasImpartidos() { return diasImpartidos; }
    public void setDiasImpartidos(String diasImpartidos) { this.diasImpartidos = diasImpartidos; }
}
