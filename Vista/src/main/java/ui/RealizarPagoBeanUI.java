package ui;

import helper.*;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import mx.desarollo.entity.*;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Named("RealizarPagoBeanUI")
@SessionScoped
public class RealizarPagoBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;

    // Variables globales
    private Double montoTotal = 0.0;
    private Double montoIngresado = 0.0;
    private Double montoFaltante = 0.0;
    private Double montoCambio = 0.0;
    private Double descuento = 0.0;
    private Date fecha;
    private byte porPagar;

    // Clases para completar el pago
    private Paga paga = new Paga();
    private Cliente cliente;
    private String idCliente;
    private Membresia nueva = new Membresia();

    private String siguienteDialogo;
    private boolean clienteTieneCredito = false;
    private double creditoAplicado = 0.0;

    // Inyectamos el login para saber quién cobra sin pedir contraseñas a cada rato
    @Inject
    private LoginBeanUI loginBeanUI;

    // Helpers necesarios
    private final PagaHelper pagaHelper = new PagaHelper();
    private final ClienteHelper clienteHelper = new ClienteHelper();
    private final AsignarClaseHelper asignarClaseHelper = new AsignarClaseHelper();
    private final ClaseHelper claseHelper = new ClaseHelper();
    private final MembresiaHelper membresiaHelper = new MembresiaHelper();

    private void calcularFaltante() {
        if (montoIngresado == null) montoIngresado = 0.0;
        if (montoTotal == null) montoTotal = 0.0;

        if (montoIngresado < montoTotal) {
            montoFaltante = montoTotal - montoIngresado;
            montoCambio = 0.0;
        } else {
            montoFaltante = 0.0;
            montoCambio = montoIngresado - montoTotal;
        }
    }

    /**
     * @Transactional asegura que todo el método sea atómico.
     * Si ocurre un throw new Exception(...), hace un Rollback automático en la BD.
     */
    @Transactional
    public void realizarPagoInteractivoMembresia() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (montoTotal < 0) throw new Exception("Monto total inválido.");
            if (montoIngresado == null || montoIngresado < 0) throw new Exception("Debe ingresar un monto para continuar.");

            calcularFaltante();

            if (montoIngresado < montoTotal) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Monto insuficiente", "Faltan " + montoFaltante + " pesos."));
                return;
            }

            montoCambio = Math.max(0.0, montoIngresado - montoTotal);

            if (cliente == null) throw new Exception("Debe seleccionar un cliente antes de realizar el pago.");

            Cliente clienteExistente = clienteHelper.obtenerCliente(cliente.getIdCliente());
            if (clienteExistente == null) {
                clienteHelper.AltaCliente(cliente);
            } else {
                cliente = clienteExistente;
            }

            Membresia membresiaActual = membresiaHelper.obtenerMembresiaPorCliente(cliente.getIdCliente(),"membresia");

            if (membresiaActual != null && membresiaActual.getFechaVencimiento() != null && membresiaActual.getFechaVencimiento().isAfter(LocalDate.now())) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Membresía activa", "El cliente ya tiene una membresía vigente hasta " + membresiaActual.getFechaVencimiento() + "."));
                return;
            }

            // Crear nueva membresía
            nueva = new Membresia();
            nueva.setFechaVencimiento(LocalDate.now().plusDays(30));
            nueva.setIdCliente(cliente);
            nueva.setTipo("membresia");
            membresiaHelper.registrarMembresia(nueva, cliente);

            cliente.setCantidadDineroMensual(800.0);
            clienteHelper.ModificarCliente(cliente);

            paga.setIdUsuariorecep(loginBeanUI.getIdUsuario()); // Tomamos el ID de la sesión activa
            paga.setFecha(LocalDate.now());
            paga.setIdCliente(cliente);
            paga.setMonto(montoTotal);
            paga.setPorPagar(porPagar);

            pagaHelper.RealizarPago(paga, nueva.getIdItem());

            PrimeFaces.current().ajax().update("formPrincipal:dlgCambio1");
            PrimeFaces.current().executeScript("PF('dlgPagoInteractivo1').hide(); PF('dlgCambio1').show();");

            finalizarTransaccion();

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al realizar el pago", e.getMessage()));
            // Al lanzar RuntimeException, le decimos a @Transactional que aborte la transacción (Rollback)
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void realizarPagoTarjetaMembresia() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (paga == null) paga = new Paga();
            if (cliente == null) throw new Exception("Debe seleccionar un cliente antes de realizar el pago.");

            Cliente clienteExistente = clienteHelper.obtenerCliente(cliente.getIdCliente());
            if (clienteExistente == null) {
                clienteHelper.AltaCliente(cliente);
            } else {
                cliente = clienteExistente;
            }

            Membresia membresiaActual = membresiaHelper.obtenerMembresiaPorCliente(cliente.getIdCliente(),"membresia");

            if (membresiaActual != null && membresiaActual.getFechaVencimiento() != null && membresiaActual.getFechaVencimiento().isAfter(LocalDate.now())) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Membresía activa", "El cliente ya tiene una membresía vigente hasta " + membresiaActual.getFechaVencimiento() + "."));
                return;
            }

            nueva = new Membresia();
            nueva.setFechaVencimiento(LocalDate.now().plusDays(30));
            nueva.setIdCliente(cliente);
            nueva.setTipo("membresia");
            membresiaHelper.registrarMembresia(nueva, cliente);

            cliente.setCantidadDineroMensual(800.0);
            clienteHelper.ModificarCliente(cliente);

            paga.setIdUsuariorecep(loginBeanUI.getIdUsuario()); // Usuario activo
            paga.setFecha(LocalDate.now());
            paga.setIdCliente(cliente);
            paga.setMonto(montoTotal);
            paga.setPorPagar(porPagar);

            pagaHelper.RealizarPago(paga, nueva.getIdItem());

            PrimeFaces.current().executeScript("PF('dlgPagoTarjeta1').hide(); PF('dlgExitoTarjeta1').show();");
            finalizarTransaccion();

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al realizar el pago con tarjeta", e.getMessage()));
            throw new RuntimeException(e); // Fuerza el Rollback
        }
    }

    @Transactional
    public void realizarPagoInteractivoClase() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (montoTotal < 0) throw new Exception("Monto total inválido.");

            String idClase = (String) fc.getExternalContext().getSessionMap().get("idClase");
            String idClienteSesion = (String) fc.getExternalContext().getSessionMap().get("idCliente");

            if (idClase == null || idClienteSesion == null) throw new Exception("No se ha seleccionado una clase o cliente.");

            this.cliente = clienteHelper.obtenerCliente(idClienteSesion);
            if (cliente == null) throw new Exception("No se encontró el cliente con ID: " + idClienteSesion);

            Clase clase = claseHelper.obtenerClase(idClase);
            if (clase == null) throw new Exception("No se encontró la clase con ID: " + idClase);

            if (clase.getClientes().size() >= clase.getCupoMaximo()) {
                throw new Exception("La clase " + clase.getNombre() + " ha alcanzado su cupo máximo.");
            }

            boolean yaAsignado = asignarClaseHelper.verificarClaseAsignadaACliente(cliente.getIdCliente(), clase.getIdClase());
            Membresia membresiaClase = membresiaHelper.obtenerMembresiaPorCliente(cliente.getIdCliente(), "clase");
            boolean paseActivo = (membresiaClase != null && membresiaClase.getFechaVencimiento().isAfter(LocalDate.now()));

            if (paseActivo) {
                if (yaAsignado) {
                    fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Clase ya asignada", "El cliente ya está inscrito y su membresia está activa."));
                    return;
                } else {
                    asignarClaseHelper.asignarClaseACliente(cliente.getIdCliente(), clase.getIdClase());
                    fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Inscrito correctamente", "El cliente ha sido inscrito a la clase."));
                    PrimeFaces.current().executeScript("PF('dlgPagoInteractivo2').hide();");
                }
            } else {
                if (montoIngresado == null || montoIngresado < 0) throw new Exception("Debe ingresar un monto.");
                calcularFaltante();
                if (montoIngresado < montoTotal) {
                    fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Monto insuficiente", "Faltan " + montoFaltante + " pesos."));
                    return;
                }

                montoCambio = Math.max(0.0, montoIngresado - montoTotal);

                paga = new Paga();
                paga.setIdUsuariorecep(loginBeanUI.getIdUsuario()); // Usuario activo
                paga.setFecha(LocalDate.now());
                paga.setIdCliente(cliente);
                paga.setMonto(montoTotal > 0 ? montoTotal : 500.00);
                paga.setPorPagar(porPagar);

                if (membresiaClase == null) {
                    nueva = new Membresia();
                    nueva.setFechaVencimiento(LocalDate.now().plusDays(30));
                    nueva.setTipo("clase");
                    nueva.setIdCliente(cliente);
                    cliente.setCantidadDineroMensual(cliente.getCantidadDineroMensual() + montoTotal);
                    clienteHelper.ModificarCliente(cliente);
                    membresiaHelper.registrarMembresia(nueva, cliente);
                    pagaHelper.RealizarPago(paga, nueva.getIdItem());
                } else {
                    membresiaClase.setFechaVencimiento(LocalDate.now().plusDays(30));
                    membresiaHelper.modificarMembresia(membresiaClase);
                    pagaHelper.RealizarPago(paga, membresiaClase.getIdItem());
                }

                PrimeFaces.current().ajax().update("formPrincipal:dlgCambio2");
                PrimeFaces.current().executeScript("PF('dlgPagoInteractivo2').hide(); PF('dlgCambio2').show();");
            }

            if (!yaAsignado) {
                asignarClaseHelper.asignarClaseACliente(cliente.getIdCliente(), clase.getIdClase());
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Asignación Exitosa", "Cliente inscrito en " + clase.getNombre() + "."));
            }

            finalizarTransaccion();

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al pagar clase", e.getMessage()));
            throw new RuntimeException(e); // Fuerza Rollback
        }
    }

    @Transactional
    public void realizarPagoTarjetaClase() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            String idClase = (String) fc.getExternalContext().getSessionMap().get("idClase");
            String idClienteSesion = (String) fc.getExternalContext().getSessionMap().get("idCliente");

            if (idClase == null || idClienteSesion == null) throw new Exception("No se ha seleccionado una clase o cliente.");

            this.cliente = clienteHelper.obtenerCliente(idClienteSesion);
            if (cliente == null) throw new Exception("No se encontró el cliente con ID: " + idClienteSesion);

            Clase clase = claseHelper.obtenerClase(idClase);
            if (clase == null) throw new Exception("No se encontró la clase con ID: " + idClase);

            if (clase.getClientes().size() >= clase.getCupoMaximo()) {
                throw new Exception("La clase " + clase.getNombre() + " ha alcanzado su cupo máximo.");
            }

            boolean yaAsignado = asignarClaseHelper.verificarClaseAsignadaACliente(cliente.getIdCliente(), clase.getIdClase());
            Membresia membresiaClase = membresiaHelper.obtenerMembresiaPorCliente(cliente.getIdCliente(), "clase");
            boolean paseActivo = (membresiaClase != null && membresiaClase.getFechaVencimiento().isAfter(LocalDate.now()));

            if (paseActivo) {
                if (yaAsignado) {
                    fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Clase ya asignada", "El cliente ya está inscrito y su membresia está activa."));
                    return;
                } else {
                    asignarClaseHelper.asignarClaseACliente(cliente.getIdCliente(), clase.getIdClase());
                    fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Inscrito correctamente", "El cliente ha sido inscrito a la clase."));
                    PrimeFaces.current().executeScript("PF('dlgPagoTarjeta2').hide();");
                }
            } else {
                paga = new Paga();
                paga.setIdUsuariorecep(loginBeanUI.getIdUsuario()); // Usuario activo
                paga.setFecha(LocalDate.now());
                paga.setIdCliente(cliente);
                paga.setMonto(montoTotal > 0 ? montoTotal : 500.00);
                paga.setPorPagar(porPagar);

                if (membresiaClase == null) {
                    nueva = new Membresia();
                    nueva.setFechaVencimiento(LocalDate.now().plusDays(30));
                    nueva.setTipo("clase");
                    nueva.setIdCliente(cliente);
                    cliente.setCantidadDineroMensual(cliente.getCantidadDineroMensual() + montoTotal);
                    clienteHelper.ModificarCliente(cliente);
                    membresiaHelper.registrarMembresia(nueva, cliente);
                    pagaHelper.RealizarPago(paga, nueva.getIdItem());
                } else {
                    membresiaClase.setFechaVencimiento(LocalDate.now().plusDays(30));
                    membresiaHelper.modificarMembresia(membresiaClase);
                    pagaHelper.RealizarPago(paga, membresiaClase.getIdItem());
                }

                PrimeFaces.current().executeScript("PF('dlgPagoTarjeta2').hide(); PF('dlgExitoTarjeta2').show();");
            }

            if (!yaAsignado) {
                asignarClaseHelper.asignarClaseACliente(cliente.getIdCliente(), clase.getIdClase());
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "¡Éxito!", "Cliente inscrito en " + clase.getNombre() + "."));
            }

            if (!paseActivo) {
                finalizarTransaccion();
            } else {
                limpiarSesionVariables();
            }

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al pagar clase", e.getMessage()));
            throw new RuntimeException(e); // Fuerza Rollback
        }
    }

    public void prepararPago(String tipo) {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            String proximoDialogoWidgetVar = fc.getExternalContext().getRequestParameterMap().get("proximo");
            if (proximoDialogoWidgetVar == null || proximoDialogoWidgetVar.isEmpty()) {
                throw new Exception("Error interno: No se especificó el diálogo de pago.");
            }
            this.siguienteDialogo = proximoDialogoWidgetVar;
            this.creditoAplicado = 0.0;
            this.cliente = null;
            this.clienteTieneCredito = false;

            if (this.idCliente != null && !this.idCliente.trim().isEmpty()) {
                String idClienteParaBuscar = this.idCliente.trim();
                this.cliente = clienteHelper.obtenerCliente(idClienteParaBuscar);
                if (this.cliente == null) throw new Exception("No se encontró el cliente con ID: " + idClienteParaBuscar);
            } else {
                String idClienteDeClase = (String) fc.getExternalContext().getSessionMap().get("idCliente");
                if (idClienteDeClase != null && !idClienteDeClase.trim().isEmpty()) {
                    this.cliente = clienteHelper.obtenerCliente(idClienteDeClase);
                    if (this.cliente == null) throw new Exception("Error de sesión: El ID de cliente '" + idClienteDeClase + "' no se encontró.");
                } else {
                    Cliente clienteEnSesion = (Cliente) fc.getExternalContext().getSessionMap().get("clienteSeleccionado");
                    if (clienteEnSesion != null) {
                        Cliente clienteFresco = clienteHelper.obtenerCliente(clienteEnSesion.getIdCliente());
                        this.cliente = (clienteFresco != null) ? clienteFresco : clienteEnSesion;
                    }
                }
            }

            if (this.cliente != null && this.cliente.getCredito() > 0.01) {
                this.clienteTieneCredito = true;
            }

            obtenerTotal(tipo);
            this.fecha = new Date();
            PrimeFaces.current().ajax().addCallbackParam("tieneCredito", this.clienteTieneCredito);

        } catch (Exception e) {
            fc.validationFailed();
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al preparar pago", e.getMessage()));
        }
    }

    public void aplicarCredito() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (cliente == null || montoTotal == null || cliente.getCredito() == 0.00) {
                throw new Exception("No se puede aplicar el crédito. Faltan datos del cliente o del monto.");
            }

            double creditoDisponible = cliente.getCredito();
            if (creditoDisponible <= 0.01) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin crédito", "El cliente no tiene crédito disponible."));
                return;
            }

            if (creditoDisponible >= montoTotal) {
                this.creditoAplicado = montoTotal;
                cliente.setCredito(creditoDisponible - montoTotal);
                montoTotal = 0.0;
            } else {
                this.creditoAplicado = creditoDisponible;
                montoTotal -= creditoDisponible;
                cliente.setCredito(0.0);
            }

            clienteHelper.ModificarCliente(cliente);
            calcularFaltante();

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Crédito aplicado", String.format("Se aplicaron $%.2f. Total a pagar: $%.2f", this.creditoAplicado, this.montoTotal)));
        } catch (Exception e) {
            fc.validationFailed();
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al aplicar crédito", e.getMessage()));
        }
    }

    public void cancelarPago() {
        try {
            limpiarCampos();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Pago cancelado", "El proceso de pago ha sido cancelado."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cancelar pago", e.getMessage()));
        }
    }

    private void limpiarSesionVariables() {
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getExternalContext().getSessionMap().remove("idClase");
        fc.getExternalContext().getSessionMap().remove("idCliente");
        fc.getExternalContext().getSessionMap().remove("clienteSeleccionado");
    }

    public void limpiarCampos() {
        cliente = null;
        idCliente = null;
        fecha = null;
        porPagar = 0;
        paga = new Paga();
        montoTotal = 0.0;
        montoIngresado = 0.0;
        montoFaltante = 0.0;
        descuento = 0.0;
        siguienteDialogo = null;
        clienteTieneCredito = false;
        creditoAplicado = 0.0;
        limpiarSesionVariables();
    }

    private void finalizarTransaccion() {
        montoIngresado = 0.0;
        montoFaltante = 0.0;
        limpiarCampos();
    }

    private String obtenerTotal(String tipo) {
        if (tipo == null) tipo = "";
        tipo = tipo.toLowerCase();

        switch (tipo) {
            case "membresia":
                montoTotal = 800.00;
                if (descuento != null && descuento > 800.00) descuento = 800.00;
                break;
            case "clase":
                montoTotal = 500.00;
                if (descuento != null && descuento > 500.00) descuento = 500.00;
                break;
            default:
                montoTotal = 0.0;
                break;
        }

        if (descuento != null && descuento > 0) {
            montoTotal = Math.max(0.0, montoTotal - descuento);
        }

        calcularFaltante();
        return tipo;
    }

    // Ponlo en cualquier parte de tu RealizarPagoBeanUI (al final de los métodos, por ejemplo)
    public void verificarAperturaAutomatica() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Boolean abrirModal = (Boolean) fc.getExternalContext().getSessionMap().get("abrirModalPagoClase");

        if (abrirModal != null && abrirModal) {
            // Borramos la bandera para que no se vuelva a abrir al recargar la página (F5)
            fc.getExternalContext().getSessionMap().remove("abrirModalPagoClase");

            // Mandamos a abrir el diálogo de método de pago 2
            PrimeFaces.current().executeScript("PF('dlgMetodoPago2').show();");
        }
    }

    // Ponlo en cualquier parte de tu RealizarPagoBeanUI (al final de los métodos, por ejemplo)
    public void verificarAperturaAutomatica1() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Boolean abrirModal = (Boolean) fc.getExternalContext().getSessionMap().get("abrirModalPagoMembresia");

        if (abrirModal != null && abrirModal) {
            // Borramos la bandera para que no se vuelva a abrir al recargar la página (F5)
            fc.getExternalContext().getSessionMap().remove("abrirModalPagoMembresia");

            // Mandamos a abrir el diálogo de método de pago 1
            PrimeFaces.current().executeScript("PF('dlgMetodoPago1').show();");
        }
    }

    public String recargar() { return "pagos.xhtml?faces-redirect=true"; }

    // Getters y Setters
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public byte getPorPagar() { return porPagar; }
    public void setPorPagar(byte porPagar) { this.porPagar = porPagar; }
    public Double getMontoTotal() { return montoTotal; }
    public Double getMontoIngresado() { return montoIngresado; }
    public void setMontoIngresado(Double montoIngresado) { this.montoIngresado = montoIngresado; calcularFaltante(); }
    public Double getMontoCambio() { return montoCambio; }
    public void setMontoCambio(Double montoCambio) { this.montoCambio = montoCambio; }
    public Double getMontoFaltante() { return montoFaltante; }
    public Double getDescuento() { return descuento; }
    public void setDescuento(Double descuento) { this.descuento = descuento; }
    public String getSiguienteDialogo() { return siguienteDialogo; }
    public boolean isClienteTieneCredito() { return clienteTieneCredito; }
}