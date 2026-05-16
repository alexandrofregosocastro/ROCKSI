package ui;

import helper.*;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.desarollo.entity.*;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Named("tiendaBeanUI")
@SessionScoped
public class TiendaBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private LoginBeanUI loginBeanUI;

    private List<ItemCarrito> carrito;
    private double total;
    private ItemCarrito itemSeleccionado;

    // Variables globales
    private Double montoTotal = 0.0;
    private Double montoIngresado = 0.0;
    private Double montoFaltante = 0.0;
    private Double montoCambio = 0.0;
    private Double descuento = 0.0;
    private Date fecha;
    private Double monto;
    private byte porPagar = 1;
    private byte pagado = 0;


    // Clases para completar el pago
    private Paga paga = new Paga();
    private Cliente cliente;
    private String idCliente;

    // Usuario recepcionista
    private String idUR;
    private String contrasenaUR;
    private Usuariorecepcionista usuarioRecepcionista;
    private String siguienteDialogo;
    private boolean clienteTieneCredito = false;
    private double creditoAplicado = 0.0;
    private boolean pagoRealizado = false;

    // Helpers necesarios
    private final PagaHelper pagaHelper = new PagaHelper();
    private final UsuarioRHelper usuarioHelper = new UsuarioRHelper();
    private final ClienteHelper clienteHelper = new ClienteHelper();
    private final ProductoHelper productoHelper = new ProductoHelper();

    public TiendaBeanUI() {
        carrito = new ArrayList<>();
        total = 0.0;
    }

    public void agregarAlCarritoDesdeProductos(SelectEvent<Producto> event) {
        Producto producto = event.getObject();

        if (producto == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se ha seleccionado un producto válido."));
            return;
        }

        if (producto.getStock() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sin stock", "El producto no tiene unidades disponibles."));
            return;
        }

        // Reducir stock
        producto.setStock(producto.getStock() - 1);

        // Buscar si ya está en el carrito
        Optional<ItemCarrito> existente = carrito.stream()
                .filter(i -> i.getId().equals(producto.getIdItem()))
                .findFirst();

        if (existente.isPresent()) {
            ItemCarrito item = existente.get();
            item.setCantidad(item.getCantidad() + 1);
        } else {
            ItemCarrito nuevo = new ItemCarrito();
            nuevo.setId(producto.getIdItem());
            nuevo.setNombre(producto.getNombre());
            nuevo.setPrecio(producto.getPrecio());
            nuevo.setCantidad(1);
            carrito.add(nuevo);
        }

        // Actualizar wl total
        calcularTotal();

        try {
            productoHelper.modificarProducto(producto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Mensaje de agregado
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Agregado", "Producto agregado al carrito."));
    }

    public void calcularTotal() {
        total = carrito.stream()
                .mapToDouble(i -> i.getPrecio() * i.getCantidad())
                .sum();
    }

    public void cobrar() {
        if (carrito.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Atención", "El carrito está vacío."));
            return;
        }

        if(pagoRealizado) {
            carrito.clear();
            total = 0.0;

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Compra realizada correctamente."));
        } else if (!pagoRealizado) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "No realizado", "No se realizó la compra."));
        }
    }

    public void devolverProductoAlInventario() {
        if (itemSeleccionado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se seleccionó un producto del carrito."));
            return;
        }

        // Buscar el producto en la BD
        Producto producto = productoHelper.obtenerProducto(itemSeleccionado.getId());
        if (producto == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontró el producto en inventario."));
            return;
        }

        //Aumentar stock en la tabla de productos
        producto.setStock(producto.getStock() + 1);

        // Reducir o eliminar del carrito
        if (itemSeleccionado.getCantidad() > 1) {
            itemSeleccionado.setCantidad(itemSeleccionado.getCantidad() - 1);
        } else {
            carrito.remove(itemSeleccionado);
        }

        calcularTotal();

        try {
            productoHelper.modificarProducto(producto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retroalimentacion de devolucion
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Actualizado", "Se devolvió una unidad al inventario."));
    }

    // Esta funcion verifica si el ID del recepcionista es valido o existente
    /**public void verificarUsuario() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            // Si el ID se deja vacio
            if (idUR == null || idUR.trim().isEmpty())
                throw new Exception("Debe ingresar el ID del usuario recepcionista.");

            // Si se ingreso algo en campo de ID en el xhtml entonces obtiene al usuario con su ID
            usuarioRecepcionista = usuarioHelper.obtenerUsuarioR(idUR.trim());
            // Si el usuario es null quiere decir que no se encontro un usuario con ese ID
            /**if (usuarioRecepcionista == null)
                throw new Exception("No se encontró un usuario con ese ID.");

            // Si se ecuentra un usuario entonces devuelve el mensaje Usuario verificado...
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario verificado", "Recepcionista encontrado."));
        } catch (Exception e) {
            // Si no se encontro entonces vuelve nula la instancia de usuarioRecepcionista y no preocede al modal de ingresar contraseña
            usuarioRecepcionista = null;
            fc.validationFailed();
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al verificar usuario", e.getMessage()));
        }
    }**/

    /**
     * Metodo para verificar al usuario primeramente el ID del UR que llamara a la instancia de usuarioHelper
     * @Throws Si el ID del UR no corresponde a ningun UR
     * @Params ninguno
     * @return void
     */
    /**public void validarContrasena() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            // Si el usuario es nulo quiere decir que primeramte no se ah encontrado el usuarioRecepcionista y que se debe enontrar para poder ingresar su contraseña
            if (usuarioRecepcionista == null)
                throw new Exception("Debe verificar primero al usuario recepcionista antes de validar la contraseña.");

            // Si la contraseña esta vacia entonces muestra el mensaje
            if (contrasenaUR == null || contrasenaUR.trim().isEmpty())
                throw new Exception("Debe ingresar la contraseña del recepcionista.");

            // Si contraseña no es agual a la contraseña que tiene el usuarioRecepcionita entonces muestra el mensaje
            if (!usuarioRecepcionista.getContrasena().equals(contrasenaUR)) {
                fc.validationFailed();
                throw new Exception("Contraseña incorrecta.");
            }

            // Si se identifica correctamente entoces muestra el siguiente mensaje
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Acceso autorizado", "El recepcionista ha sido autenticado correctamente."));
        } catch (Exception e) {
            // Si no, entonces muestra el siguiente mensaje
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de autenticación", e.getMessage()));
        }
    }*/

    /**
     * Metodo para realizar un pago en efectivo de carrito
     * @Throws Si algun dato es null o hay un error al realizar el pago
     * @Params ninguno
     * @return void
     */
    public void realizarPagoInteractivoCarrito() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            // Valida recepcionista
            /*if (usuarioRecepcionista == null)
                throw new Exception("Debe validar un recepcionista antes de realizar el pago.");*/

            // Valida productos en el carrito
            if (carrito == null || carrito.isEmpty()) {
                throw new Exception("No hay productos en el carrito para pagar.");
            }

            // Valida monto ingresado
            if (montoIngresado == null || montoIngresado < 0)
                throw new Exception("Debe ingresar un monto para continuar.");

            // Compara monto ingresado contra el monto total a pagar
            if (montoIngresado < this.montoTotal) {
                montoFaltante = this.montoTotal - montoIngresado;
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Monto insuficiente", "Faltan " + montoFaltante + " pesos."));
                return;
            }

            // Calcula el cambio
            montoCambio = montoIngresado - this.montoTotal;
            if (montoCambio < 0) montoCambio = 0.0;

            // Valida cliente
            if (cliente == null)
                throw new Exception("Debe seleccionar un cliente antes de realizar el pago.");

            // Obtener cliente de la BD con la funcion obtenerCliente() del clienteHelper
            Cliente clienteExistente = clienteHelper.obtenerCliente(cliente.getIdCliente());
            if (clienteExistente == null) {
                throw new Exception("El cliente seleccionado no existe...");
            } else {
                cliente = clienteExistente;
            }

            // Calcula el total
            calcularTotal();

            double ratioDePago = 1.0;

            // Calcula el ratio de pago (evitando division por cero)
            if (this.total > 0.01) {
                ratioDePago = this.montoTotal / this.total;
            } else if (this.montoTotal > 0.01) {
                // Subtotal 0 pero se cobra algo
                // Por seguridad no aplicamos ratio
            } else {
                // Gratis
            }

            for (ItemCarrito item : carrito) {
                Paga pagaItem = new Paga();
                pagaItem.setIdUsuariorecep(loginBeanUI.getIdUsuario());// Toma el id del Usuario que haya iniciado sesion
                pagaItem.setFecha(LocalDate.now());
                pagaItem.setIdCliente(cliente);

                // Calcula el monto original del item
                double montoItemOriginal = item.getPrecio() * item.getCantidad();

                // Aplica el ratio para obtener el monto real pagado por este item
                double montoItemPagado = montoItemOriginal * ratioDePago;

                // Aplico un redondedeo de 2 decimales
                double montoRedondeado = Math.round(montoItemPagado * 100.0) / 100.0;

                pagaItem.setMonto(montoRedondeado);
                pagaItem.setPorPagar(pagado);
                pagaHelper.RealizarPago(pagaItem, item.getId());
            }

            this.pagoRealizado = true;
            cobrar();

            // Actualizo la UI y limpio las variables utilizadas
            PrimeFaces.current().ajax().update("formPrincipal:dlgCambio1");
            PrimeFaces.current().executeScript("PF('dlgPagoInteractivo1').hide(); PF('dlgCambio1').show();");
            PrimeFaces.current().ajax().update("formProductos");

            montoIngresado = 0.0;
            montoFaltante = 0.0;
            limpiarCampos();
            carrito.clear();
            calcularTotal();

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al realizar el pago", e.getMessage()));
        }
    }

    /**
     * Metodo para realizar un pago del carrito con tarjeta
     * @Throws Si algun dato es null o hay un error al realizar el pago
     * @Params ninguno
     * @return void
     */
    public void realizarPagoTarjetaCarrito() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            // Valida recepcionista
            /*if (usuarioRecepcionista == null)
                throw new Exception("Debe validar un recepcionista antes de realizar el pago.");*/

            // Valida cliente
            if (cliente == null)
                throw new Exception("Debe seleccionar un cliente antes de realizar el pago.");

            // Obtener cliente de la BD con la funcion obtenerCliente() del clienteHelper
            Cliente clienteExistente = clienteHelper.obtenerCliente(cliente.getIdCliente());
            if (clienteExistente == null) {
                throw new Exception("El cliente seleccionado no existe...");
            }
            cliente = clienteExistente;

            // Valida productos en el carrito
            if (carrito.isEmpty()) {
                throw new Exception("No hay productos en el carrito para pagar.");
            }

            // Calcula el total
            calcularTotal();

            double ratioDePago = 1.0;

            // Calcula el ratio de pago (evitando division por cero)
            if (this.total > 0.01) {
                ratioDePago = this.montoTotal / this.total;
            } else if (this.montoTotal > 0.01) {
                // Subtotal 0 pero se cobra algo
                // Por seguridad no aplicamos ratio
            } else {
                // Gratis
            }

            for (ItemCarrito item : carrito) {
                Paga pagaItem = new Paga();
                pagaItem.setIdUsuariorecep(loginBeanUI.getIdUsuario());// Toma el id del Usuario que haya iniciado sesion
                pagaItem.setFecha(LocalDate.now());
                pagaItem.setIdCliente(cliente);

                // Calculo el monto original del item
                double montoItemOriginal = item.getPrecio() * item.getCantidad();

                // Aplica el ratio para obtener el monto real pagado por este item
                double montoItemPagado = montoItemOriginal * ratioDePago;

                // Aplico un redondedeo de 2 decimales
                double montoRedondeado = Math.round(montoItemPagado * 100.0) / 100.0;

                pagaItem.setMonto(montoRedondeado);
                pagaItem.setPorPagar(pagado);
                pagaHelper.RealizarPago(pagaItem, item.getId());
            }

            // Indico que el pago se realizo con exito
            this.pagoRealizado = true;
            cobrar();

            // Actualizo la UI y limpio las variables utilizadas
            PrimeFaces.current().executeScript("PF('dlgPagoTarjeta1').hide(); PF('dlgExitoTarjeta1').show();");
            PrimeFaces.current().ajax().update("formProductos");

            limpiarCampos();
            carrito.clear();
            calcularTotal();

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al realizar el pago con tarjeta", e.getMessage()));
        }
    }

    /**
     * Metodo para realizar un pago por pagar del carrito
     * @Throws Si algun dato es null o hay un error al realizar el pago
     * @Params ninguno
     * @return void
     */
    public void realizarPagoPorPagar() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            // Valida recepcionista
            /*if (usuarioRecepcionista == null)
                throw new Exception("Debe validar un recepcionista antes de realizar el pago.");*/

            // Valida cliente
            if (cliente == null)
                throw new Exception("Debe seleccionar un cliente antes de realizar el pago.");

            // Obtener cliente de la BD con la funcion obtenerCliente() del clienteHelper
            Cliente clienteExistente = clienteHelper.obtenerCliente(cliente.getIdCliente());
            if (clienteExistente == null) {
                throw new Exception("El cliente seleccionado no existe...");
            }
            cliente = clienteExistente;

            // Valida productos en el carrito
            if (carrito.isEmpty()) {
                throw new Exception("No hay productos en el carrito para pagar.");
            }

            // Calcula el total
            calcularTotal();

            double ratioDePago = 1.0;

            // Calcula el ratio de pago (evitando division por cero)
            if (this.total > 0.01) {
                ratioDePago = this.montoTotal / this.total;
            } else if (this.montoTotal > 0.01) {
                // Subtotal 0 pero se cobra algo
                // Por seguridad no aplicamos ratio
            } else {
                // Gratis
            }

            for (ItemCarrito item : carrito) {
                Paga pagaItem = new Paga();
                pagaItem.setIdUsuariorecep(loginBeanUI.getIdUsuario()); // Toma el id del Usuario que haya iniciado sesion
                pagaItem.setFecha(LocalDate.now());
                pagaItem.setIdCliente(cliente);

                // Calculo el monto original del item
                double montoItemOriginal = item.getPrecio() * item.getCantidad();

                // Aplica el ratio para obtener el monto real pagado por este item
                double montoItemPagado = montoItemOriginal * ratioDePago;

                // Aplico un redondedeo de 2 decimales
                double montoRedondeado = Math.round(montoItemPagado * 100.0) / 100.0;

                pagaItem.setMonto(montoRedondeado);
                pagaItem.setPorPagar(Byte.parseByte("1"));
                pagaHelper.RealizarPago(pagaItem, item.getId());
            }

            // Indico que el pago se realizo con exito
            this.pagoRealizado = true;
            cobrar();

            // Actualizo la UI y limpio las variables utilizadas
            PrimeFaces.current().executeScript("PF('dlgPagoPorPagarM').hide(); PF('dlgExitoPorPagar').show();");
            PrimeFaces.current().ajax().update("formProductos");

            limpiarCampos();
            carrito.clear();
            calcularTotal();

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al realizar el pago con tarjeta", e.getMessage()));
        }
    }

    /**
     * Metodo para preparar el pago y sus variables
     * @Throws Si algun dato es null o hay un error durante el proceso
     * @return void
     */
    public void prepararPago() {

        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            // Obtiene el nombre del dialogo de pago
            String proximoDialogoWidgetVar = fc.getExternalContext().getRequestParameterMap().get("proximo");
            if (proximoDialogoWidgetVar == null || proximoDialogoWidgetVar.isEmpty()) {
                throw new Exception("Error interno: No se especificó el diálogo de pago.");
            }
            this.siguienteDialogo = proximoDialogoWidgetVar;

            // Reinicia los valores
            this.creditoAplicado = 0.0;
            this.cliente = null;
            this.clienteTieneCredito = false;

            // Si se escribio un id cliente
            if (this.idCliente != null && !this.idCliente.trim().isEmpty()) {

                // Si si se ecribio, obtiene el cliente por su Id
                String idClienteParaBuscar = this.idCliente.trim();
                this.cliente = clienteHelper.obtenerCliente(idClienteParaBuscar);

                if (this.cliente == null) {
                    // El UR escribió un ID de un cliente que no existe
                    throw new Exception("No se encontró el cliente con ID: " + idClienteParaBuscar);
                }

                if (this.cliente.getEstatus()==0) {
                    // El UR escribió un ID de un cliente que esta eliminado logicamente
                    throw new Exception("No se puede realizar una venta a un cliente eliminado...");
                }

            }else{
                    throw new Exception("Se debe seleccionar un cliente...");
            }

            // Solo se revisa credito (si tenemos un cliente existente)
            if (this.cliente != null && this.cliente.getCredito() > 0.01) {
                this.clienteTieneCredito = true;
            }

            // Calcular total
            obtenerTotal();

            this.fecha = new Date();
            PrimeFaces.current().ajax().addCallbackParam("tieneCredito", this.clienteTieneCredito);

        } catch (Exception e) {
            fc.validationFailed();
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al preparar pago", e.getMessage()));
        }
    }

    /**
     * Metodo para aplicar credito si el cliente tiene y aplicarlo al total
     * @Throws Si algun dato es null o hay un error durante el proceso
     * @return void
     */
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
                montoTotal = montoTotal - creditoDisponible;
                cliente.setCredito(0.0);
            }

            clienteHelper.ModificarCliente(cliente);
            calcularFaltante();

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Crédito aplicado",
                    String.format("Se aplicaron $%.2f. Total a pagar: $%.2f", this.creditoAplicado, this.montoTotal)));

        } catch (Exception e) {
            fc.validationFailed();
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al aplicar crédito", e.getMessage()));
        }
    }

    /**
     * Metodo para cancelar el pago
     * @Throws Si hay un error durante el proceso
     * @return void
     */
    public void cancelarPago() {
        try {
            limpiarCampos();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Pago cancelado", "El proceso de pago ha sido cancelado."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cancelar pago", e.getMessage()));
        }
    }

    /**
     * Metodo para limpiar variables
     * @Throws ninguno
     * @Params ninguno
     * @return void
     */
    public void limpiarCampos() {
        cliente = null;
        monto = null;
        fecha = null;
        porPagar = 0;
        contrasenaUR = null;
        idUR = null;
        usuarioRecepcionista = null;
        paga = new Paga();
        montoTotal = 0.0;
        montoIngresado = 0.0;
        montoFaltante = 0.0;
        descuento = 0.0;
        siguienteDialogo = null;
        clienteTieneCredito = false;
        creditoAplicado = 0.0;
    }

    /**
     * Metodo para obtener el total a pagar
     * @Throws Si algun dato es null o hay un error durante el proceso
     * @Return void
     */
    private void obtenerTotal() {

        // Obtiene el subtotal del carrito
        calcularTotal(); // Aseguro que el total este actualizado

        // Establesco el monto base a pagar
        montoTotal = this.total;

        // Valida y ajusta el descuento (si es mayor al total)
        if (descuento != null && descuento > montoTotal) {
            descuento = montoTotal;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Descuento ajustado",
                            "El descuento no puede ser mayor al monto de la venta."));
        }

        // Aplica el descuento al monto total
        if (descuento != null && descuento > 0) {
            montoTotal -= descuento;
            if (montoTotal < 0) montoTotal = 0.0;
        }

        // Calcular lo que falta por pagar (basado en el monto ya con descuento)
        calcularFaltante();
    }

    /**
     * Metodo para calcular el Faltante a la hora de hacer pago interactivo
     * @Params ninguno
     * @return void
     */
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

    public void validarCarrito() { //Esta funcion evita que se pueda entrar a cobrar sin productos en el carrito.
        FacesContext fc = FacesContext.getCurrentInstance();
        if (carrito == null || carrito.isEmpty()) {
            fc.validationFailed(); // Esto le avisa a primefaces que la validación fallo
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Carrito vacío", "Agrega al menos un producto antes de cobrar."));
        }
    }

    /**
     * Metodo para Agrega un producto nuevo al carrito
     */
    public void agregarAlCarrito(Producto producto) {
        if (producto == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Producto no válido."));
            return;
        }

        if (producto.getStock() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sin stock", "El producto no tiene unidades disponibles."));
            return;
        }

        // Descontamos 1 del stock real
        producto.setStock(producto.getStock() - 1);

        // Buscamos si ya existe en el carrito
        Optional<ItemCarrito> existente = carrito.stream()
                .filter(i -> i.getId().equals(producto.getIdItem()))
                .findFirst();

        if (existente.isPresent()) {
            ItemCarrito item = existente.get();
            item.setCantidad(item.getCantidad() + 1);
        } else {
            ItemCarrito nuevo = new ItemCarrito();
            nuevo.setId(producto.getIdItem());
            nuevo.setNombre(producto.getNombre());
            nuevo.setPrecio(producto.getPrecio());
            nuevo.setCantidad(1);
            carrito.add(nuevo);
        }

        calcularTotal();

        try {
            productoHelper.modificarProducto(producto);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Agregado", "Producto agregado al carrito."));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo para aunmentar la cantidad de un mismo producto
     */
    public void aumentarCantidad(ItemCarrito itemCarrito) {
        if (itemCarrito == null) return;

        // Necesitamos traernos el producto de la BD para verificar si todavía queda stock
        Producto producto = productoHelper.obtenerProducto(itemCarrito.getId());

        if (producto == null || producto.getStock() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Límite alcanzado", "Ya no hay más unidades en stock."));
            return;
        }

        // Subimos en el carrito, bajamos en la BD
        itemCarrito.setCantidad(itemCarrito.getCantidad() + 1);
        producto.setStock(producto.getStock() - 1);

        calcularTotal();

        try {
            productoHelper.modificarProducto(producto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo para disminuir la cantidad de un mismo producto
     */
    public void disminuirCantidad(ItemCarrito itemCarrito) {
        if (itemCarrito == null) return;

        if (itemCarrito.getCantidad() > 1) {
            // Si tiene mas de 1, restamos en carrito y devolvemos 1 a la BD
            itemCarrito.setCantidad(itemCarrito.getCantidad() - 1);

            Producto producto = productoHelper.obtenerProducto(itemCarrito.getId());
            if (producto != null) {
                producto.setStock(producto.getStock() + 1);
                try {
                    productoHelper.modificarProducto(producto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            calcularTotal();
        } else {
            // Si solo tiene 1 y le da al menos, eliminamos el item del carrito
            eliminarDelCarrito(itemCarrito);
        }
    }

    /**
     * Metodo Para elimianar un producto, sin importar cantidad
     */
    public void eliminarDelCarrito(ItemCarrito itemCarrito) {
        if (itemCarrito == null) return;

        // Devolvemos TODA la cantidad que tenía en el carrito de regreso a la BD
        Producto producto = productoHelper.obtenerProducto(itemCarrito.getId());
        if (producto != null) {
            producto.setStock(producto.getStock() + itemCarrito.getCantidad());
            try {
                productoHelper.modificarProducto(producto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Eliminamos de la lista temporal y recalculamos totales
        carrito.remove(itemCarrito);
        calcularTotal();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Eliminado", "Se retiró el producto del carrito."));
    }

    // Getters y setters
    public List<ItemCarrito> getCarrito() { return carrito; }
    public void setCarrito(List<ItemCarrito> carrito) { this.carrito = carrito; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public ItemCarrito getItemSeleccionado() { return itemSeleccionado; }
    public void setItemSeleccionado(ItemCarrito itemSeleccionado) { this.itemSeleccionado = itemSeleccionado;}

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public byte getPorPagar() { return porPagar; }
    public void setPorPagar(byte porPagar) { this.porPagar = porPagar; }

    public String getIdUR() { return idUR; }
    public void setIdUR(String idUR) { this.idUR = idUR; }

    public String getContrasenaUR() { return contrasenaUR; }
    public void setContrasenaUR(String contrasenaUR) { this.contrasenaUR = contrasenaUR; }

    public Usuariorecepcionista getUsuarioRecepcionista() { return usuarioRecepcionista; }

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
