package ui;

import helper.MovimientoHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.desarollo.entity.MovimientoCaja;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.LocalDateTime;

@Named("inicioCajaBeanUI")
@SessionScoped
public class InicioCajaBeanUI implements Serializable {

    @Inject
    private LoginBeanUI loginBeanUI;

    private Double montoEnCaja;

    private final MovimientoHelper movimientoHelper = new MovimientoHelper();

    public void registrarAperturaCaja() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {

            String idUsuarioActivo = loginBeanUI.getIdUsuario();

            if (idUsuarioActivo == null || idUsuarioActivo.trim().isEmpty()) {
                throw new Exception("Por seguridad, vuelve a iniciar sesión para realizar esta acción.");
            }

            if (montoEnCaja == null || montoEnCaja <= 0) {
                throw new Exception("El monto de apertura debe ser mayor a 0.");
            }

            if (movimientoHelper.existeAperturaHoy()) {

                // Si no es admin, bloqueamos el que vuelva a registrar una apertura de caja
                if (!loginBeanUI.isAdmin()) {
                    throw new Exception("La caja ya fue abierta hoy. Solo un Administrador puede corregir el monto.");
                }

                // Si es admin, actualizamos el registro
                MovimientoCaja aperturaExistente = movimientoHelper.obtenerAperturaHoy();
                aperturaExistente.setMonto(montoEnCaja);
                aperturaExistente.setIdUsuario(idUsuarioActivo); // Actualizamos a quién hizo la correccion
                aperturaExistente.setRolUsuario("ADMINISTRADOR");
                aperturaExistente.setObservaciones("Monto corregido por el Administrador");

                movimientoHelper.actualizarMovimiento(aperturaExistente);

                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Corregido", "El monto de apertura de hoy fue actualizado a $" + montoEnCaja));

            } else {
                // Si no existe una apertura hoy, hacemos el registro
                MovimientoCaja apertura = new MovimientoCaja();
                apertura.setIdUsuario(idUsuarioActivo);
                apertura.setRolUsuario(loginBeanUI.isAdmin() ? "ADMINISTRADOR" : "RECEPCIONISTA");
                apertura.setTipoMovimiento("APERTURA");
                apertura.setMonto(montoEnCaja);
                apertura.setFechaHora(LocalDateTime.now());
                apertura.setObservaciones("Apertura de caja inicial");

                movimientoHelper.registrarMovimiento(apertura);

                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Apertura de caja registrada por $" + montoEnCaja));
            }

            // Actualizamos vista y cerramos dialogo
            PrimeFaces.current().ajax().update("formPagos:tablaPagos");
            PrimeFaces.current().executeScript("PF('dlgMontoApertura').hide(); PF('dlgConfirmacionApertura').show();");

            limpiar();

        } catch (Exception e) {
            fc.addMessage("msgsMontoApertura",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Registrar", e.getMessage()));
            e.printStackTrace();
        }
    }

    public void limpiar() {
        this.montoEnCaja = null;
    }

    // Getters y Setters
    public Double getMontoEnCaja() { return montoEnCaja; }
    public void setMontoEnCaja(Double montoEnCaja) { this.montoEnCaja = montoEnCaja; }
}