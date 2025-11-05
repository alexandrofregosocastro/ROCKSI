package ui;

import helper.ClaseHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.desarollo.entity.Clase;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Named("altaClaBeanUI") //Nombre que se usa en el xhtml
@SessionScoped
public class AltaClaseBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private int cupoMaximo;
    private String maestro;
    private String horaInicio;
    private String horaFinal;
    private String dias;

    private Date horaInicioDate;
    private Date horaFinalDate;

    private final ClaseHelper claseHelper = new ClaseHelper();

    public void altaClase() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            // validaciones
            if (nombre == null || nombre.trim().isEmpty())
                throw new Exception("El campo Nombre es obligatorio.");

            if (horaInicioDate == null || horaFinalDate == null)
                throw new Exception("Debe seleccionar hora de inicio y hora final.");

            if (cupoMaximo <= 0)
                throw new Exception("El campo Cupo máximo debe ser mayor a 0.");

            if (maestro == null || maestro.trim().isEmpty())
                throw new Exception("El campo Maestro es obligatorio.");

            // formatear horas
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String horaInicioStr = sdf.format(horaInicioDate);
            String horaFinalStr = sdf.format(horaFinalDate);

            // validacion de rango de horas
            Date inicio = sdf.parse(horaInicioStr);
            Date fin = sdf.parse(horaFinalStr);
            if (!inicio.before(fin))
                throw new Exception("La hora de inicio no puede ser mayor o igual a la hora final.");

            // concatenar el horario
            String horario = horaInicioStr + "-" + horaFinalStr;

            // crear la clase
            Clase nuevaClase = new Clase();
            nuevaClase.setNombre(nombre.trim());
            nuevaClase.setHorario(horario);
            nuevaClase.setCupoMaximo(cupoMaximo);
            nuevaClase.setMaestro(maestro.trim());
            nuevaClase.setDias(dias.trim());
            nuevaClase.setIdUsuarioAdmin("ADM1000"); //Este valor se va a tener que cambiar despues, cuando exista el usuario admin
            nuevaClase.setTipo("clase");

            claseHelper.AltaClase(nuevaClase);

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro exitoso", "La clase fue agregada correctamente."));
           // PrimeFaces.current().ajax().update("formClases:tablaClases formClases:msgsClase");

            limpiarCampos();

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al registrar clase", e.getMessage()));
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

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFinal() { return horaFinal; }
    public void setHoraFinal(String horaFinal) { this.horaFinal = horaFinal; }

    public int getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = cupoMaximo; }

    public String getMaestro() { return maestro; }
    public void setMaestro(String maestro) { this.maestro = maestro; }

    public String getDias() { return dias; }
    public void setDias(String dias) { this.dias = dias; }

    //Esta funcion pone en blanco los campos del formulario de xhtml
    private void limpiarCampos() {
        nombre = "";
        horaInicio = null;
        horaFinal = null;
        cupoMaximo = 0;
        maestro = "";
        horaInicioDate = null;
        horaFinalDate = null;
        dias = "";
    }
}
