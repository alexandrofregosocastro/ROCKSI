package ui;

import helper.EmailHelper;
import helper.PagaHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.desarollo.entity.Cliente;
import mx.desarollo.entity.Paga;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Named("pagoBeanUI")
@SessionScoped
public class PagaBeanUI implements Serializable {

    private List<Paga> listaPagos;
    private String filtro;
    private String idPagoCancelar;
    private final PagaHelper pagaHelper = new PagaHelper();

    @PostConstruct
    public void init() {
        this.filtro = "";
        cargarPagos();
    }

    public void recargar() {
        this.filtro = "";
        cargarPagos();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Actualizado", "Tabla de pagos refrescada."));
    }

    public void filtrarPorId() {
        try {
            if (filtro == null || filtro.trim().isEmpty()) {
                recargar();
            } else {
                listaPagos = pagaHelper.buscarPagosPorId(filtro.trim());
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al filtrar pagos."));
        }
        PrimeFaces.current().ajax().update("formPagos:tablaPagos");
    }

    public void cargarPagos() {
        try {
            listaPagos = pagaHelper.listarPagos();
            if (listaPagos == null || listaPagos.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "No hay pagos registrados."));
            }
        } catch (Exception e) {
            listaPagos = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar los pagos."));
            e.printStackTrace();
        }
    }

    public void abrirDialogoCancelarPago() {
        PrimeFaces.current().executeScript("PF('dlgCancelarPago').show()");
    }

    public void cancelarPago() {
        try {
            if (idPagoCancelar == null || idPagoCancelar.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Debe ingresar un ID de pago."));
                return;
            }

            Paga original = pagaHelper.obtenerPaga(idPagoCancelar.trim());
            if (original == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontró el pago con ese ID."));
                return;
            }

            Paga inverso = new Paga();
            inverso.setIdUsuariorecep(original.getIdUsuariorecep());
            inverso.setFecha(LocalDate.now());
            inverso.setIdCliente(original.getIdCliente());
            inverso.setMonto(-1 * original.getMonto());
            inverso.setPorPagar(original.getPorPagar());

            // Registrar el pago inverso en la BD
            pagaHelper.RealizarPago(inverso, original.getIdItem().getIdItem());

            // Enviar correo a cliente
            try {
                Cliente cliente = original.getIdCliente();
                String correoCliente = cliente.getCorreoElectronico();

                if (correoCliente != null && !correoCliente.trim().isEmpty()) {
                    String asunto = "Aviso de Cancelación de Pago - Rock On";
                    String mensaje = "Hola " + cliente.getNombreCompleto() + ",\n\n"
                            + "Te informamos que tu pago con ID " + original.getIdPaga()
                            + " por un monto de $" + original.getMonto()
                            + " ha sido cancelado exitosamente en nuestro sistema.\n\n"
                            + "Si tienes alguna duda, acércate a recepción.\n\n"
                            + "Saludos,\nEl equipo de Rock On.";

                            EmailHelper.enviarCorreo(correoCliente, asunto, mensaje);

                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontró el pago con ese ID."));

                    // Esto se debera cambiar por un toast en la vista ya que tengamos el correo del cliente
                    System.out.println("Correo de cancelación enviado exitosamente a: " + correoCliente);
                } else {
                    System.out.println("El cliente " + cliente.getIdCliente() + " no tiene correo registrado.");
                }
            } catch (Exception exMail) {
                // Atrapamos el error si surge uno
                System.err.println("Error al intentar enviar el correo de cancelación: " + exMail.getMessage());
            }

            cargarPagos();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Pago cancelado correctamente."));
            PrimeFaces.current().ajax().update("formPagos");

            PrimeFaces.current().executeScript("PF('dlgCancelarPago').hide();");

            idPagoCancelar = null;

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al cancelar el pago."));
        }
    }

    public void aceptarPagoAdelantado() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Pago adelantado aceptado."));
    }

    public void inicioCorte() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Inicio de corte registrado."));
    }

    public void tomarDineroCaja() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Dinero tomado de la caja."));
    }

    public List<Paga> getListaPagos() {
        return listaPagos;
    }

    public void setListaPagos(List<Paga> listaPagos) {
        this.listaPagos = listaPagos;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getIdPagoCancelar() {
        return idPagoCancelar;
    }

    public void setIdPagoCancelar(String idPagoCancelar) {
        this.idPagoCancelar = idPagoCancelar;
    }
}
