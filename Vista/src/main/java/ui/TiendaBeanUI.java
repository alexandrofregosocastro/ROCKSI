package ui;

import helper.ProductoHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.desarollo.entity.ItemCarrito;
import mx.desarollo.entity.Producto;
import org.primefaces.event.SelectEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Named("tiendaBeanUI")
@SessionScoped
public class TiendaBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ItemCarrito> carrito;
    private double total;
    private ItemCarrito itemSeleccionado;


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

        carrito.clear();
        total = 0.0;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Compra realizada correctamente."));
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


    // Getters y setters

    public List<ItemCarrito> getCarrito() {
        return carrito;
    }

    public void setCarrito(List<ItemCarrito> carrito) {
        this.carrito = carrito;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public ItemCarrito getItemSeleccionado() {
        return itemSeleccionado;
    }
    public void setItemSeleccionado(ItemCarrito itemSeleccionado) {
        this.itemSeleccionado = itemSeleccionado;
    }
}
