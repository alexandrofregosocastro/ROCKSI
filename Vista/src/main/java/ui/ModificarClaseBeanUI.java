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
import ui.ClaseBeanUI;

import mx.desarollo.entity.Clase;
import mx.desarollo.entity.Cliente;
import org.primefaces.PrimeFaces;

@Named("modificarClaBeanUI")
@SessionScoped
public class ModificarClaseBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;

    private Clase clase = new Clase();
    private final ClaseHelper guardarClase = new ClaseHelper();

    // Campos que enlaza el formulario de modificacion
    private String nombre;
    private String horaInicio;
    private String horaFinal;
    private int cupoMaximo;
    private String maestro;
    private String diasImpartidos;
    private Date horaInicioDate;
    private Date horaFinalDate;


    // String donde se guardara el ID de la clase a buscar
    private String busquedaId;

    /**
     * Metodo para cargar los datos de una clase por su ID que llamara a la instancia de ClaseHelper
     * @Throws Si la base de datos rechaza la peticion o si no se encutra la clase por su ID
     * @return void
     */
    public void cargarClasePorId() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            // Si el campo busquedaId esta vacio entonces
            if (busquedaId == null || busquedaId.trim().isEmpty()) {
                fc.addMessage("msgsAsk", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ingrese un ID de clase válido"));
                PrimeFaces.current().ajax().addCallbackParam("found", false);
                return; // Sale del metodo
            }

            // Si la String busquedaId contiene algun dato se lo asignamos a la String id
            String id = busquedaId.trim();
            // Buscamos la clase por su id y la clase que nos retorne el metodo.obtenerClase(id) se lo asignamos a un objeto de tipo Clase
            Clase cla = guardarClase.obtenerClase(id);

            // Si la clase es null entonces, quiere decir que no encontro ninguna clase con el ID
            if (cla == null) {
                fc.addMessage("msgsAsk", new FacesMessage(FacesMessage.SEVERITY_ERROR, "No encontrado", "No existe clase con ID " + id));
                PrimeFaces.current().ajax().addCallbackParam("found", false);
                return; // Sale del metodo
            }

            // si existe, precarga los datos de la clase en la ventana de modificar
            this.clase = cla;
            this.nombre = cla.getNombre();
            this.cupoMaximo = cla.getCupoMaximo();
            this.maestro = cla.getMaestro();
            this.diasImpartidos = cla.getDias();
            String horario = cla.getHorario();

            // Bloques de codigo para separar la hora de inicio y final en la que se imparte la clase dependiendo de como se ingrese:
            // Si es por ejemplo de 10:00 a 11:00 con a (letra a)
            if (horario != null && horario.contains("a")) {
                String[] partes = horario.split("a");

                String horaInicio = partes[0].trim();
                String horaFinal = partes[1].trim();
                if (horaInicio.length() == 4) horaInicio = "0" + horaInicio;
                if (horaFinal.length() == 4) horaFinal = "0" + horaFinal;

                this.horaInicio = horaInicio;
                this.horaFinal = horaFinal;

                // Si es por ejemplo de 10:00 - 11:00, con - (guión)
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

    /**
     * Metodo para modificar los datos de una clase que llamara a la instancia de ClaseHelper
     * @Throws Si la base de datos rechaza la peticion de modificacion, ya sea por valor invalido
     * @return void
     */
    public void modificarClase() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            // Asegura que la clase no este vacia, si la clase esta vacia entonces
            if (this.clase == null || this.clase.getIdClase() == null || this.getClase().getIdClase().trim().isEmpty()) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay clase cargada para modificar."));
                return; // Sale del metodo
            }

            // Formateo la hora con el patron HH:mm
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
            // parseo a LocalTime la horaInicio que especifico el Usuario en el campo y tambien aplico el formato
            LocalTime inicio = LocalTime.parse(horaInicio, formato);
            // parseo a LocalTime la horaFinal que especifico el Usuario en el campo y tambien aplico el formato
            LocalTime fin = LocalTime.parse(horaFinal, formato);

            // Si la Hora de inicio no es anterior a la hora final entonces
            if (!inicio.isBefore(fin)) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error", "La hora de inicio no debe ser mayor o igual que la hora final."));
                return; // Sale del metodo
            }

            // Asigno valores ingresados por el Usuario a la clase antes encontrada
            this.clase.setNombre(((this.nombre == null) ? "" : this.nombre.trim()));
            this.clase.setHorario(horaInicio + " a " + horaFinal);
            this.clase.setCupoMaximo(cupoMaximo);
            this.clase.setMaestro(((this.maestro == null) ? "" : this.maestro.trim()));
            this.clase.setDias(((this.diasImpartidos == null) ? "" : this.diasImpartidos.trim()));

            // Modifico la clase con el metodo .modificarClase de ClaseHelper
            guardarClase.modificarClase(this.clase);

            ClaseBeanUI claseBeanUI = (ClaseBeanUI) fc.getApplication()
                    .getELResolver().getValue(fc.getELContext(), null, "claseBeanUI");
            if (claseBeanUI != null) claseBeanUI.cargarClases();


            // Si se modifico correctamente entonces muestro Modificacion Exitosa
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificación exitosa", "Clase modificada correctamente."));

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Modificación inválida", e.getMessage()));
        }
    }

    public void cargarClaseDesdeTabla(Clase c) {
        this.clase = c;
        this.nombre = c.getNombre();
        this.maestro = c.getMaestro();
        this.cupoMaximo = c.getCupoMaximo();
        this.diasImpartidos = c.getDias();
        if (c.getHorario() != null && c.getHorario().contains("-")) {
            String[] partes = c.getHorario().split("-");
            this.horaInicio = partes[0].trim();
            this.horaFinal = partes[1].trim();
        }
    }

    // Getters y Setters
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
}
