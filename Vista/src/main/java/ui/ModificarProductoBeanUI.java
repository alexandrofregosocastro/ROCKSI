package ui;

import helper.ProductoHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.desarollo.entity.Producto;
import org.primefaces.PrimeFaces;

import java.io.Serializable;

@Named("modificarProdBeanUI")
@SessionScoped
public class ModificarProductoBeanUI implements Serializable {

    private String busquedaId;
    private String nombre;
    private Integer stock;
    private Double precio;
    private String proveedor;

    private Producto productoEncontrado;
    private final ProductoHelper productoHelper = new ProductoHelper();

    //precargar los datos del producto en el dialog
    public void cargarProductoPorId() {
        FacesContext fc = FacesContext.getCurrentInstance();
        PrimeFaces pf = PrimeFaces.current();

        try {
            if (busquedaId == null || busquedaId.trim().isEmpty()) {
                throw new Exception("Debe ingresar un ID de producto.");
            }

            productoEncontrado = productoHelper.obtenerProducto(busquedaId.trim());

            if (productoEncontrado == null) {
                throw new Exception("No se encontró ningún producto con el ID: " + busquedaId);
            }

            // Precargar los datos en los inputs
            this.nombre = productoEncontrado.getNombre();
            this.stock = productoEncontrado.getStock();
            this.precio = productoEncontrado.getPrecio();
            this.proveedor = productoEncontrado.getProveedor();

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Producto encontrado", "Los datos han sido cargados correctamente."));

            pf.ajax().addCallbackParam("found", true);

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            pf.ajax().addCallbackParam("found", false);
        }
    }

    //metodo para modificar el producto
    public void modificarProducto() {
        FacesContext fc = FacesContext.getCurrentInstance();

        try {
            if (productoEncontrado == null) {
                throw new Exception("Debe buscar un producto antes de modificarlo.");
            }

            productoEncontrado.setNombre(nombre.trim());
            productoEncontrado.setStock(stock);
            productoEncontrado.setPrecio(precio);
            productoEncontrado.setProveedor(proveedor.trim());

            productoHelper.modificarProducto(productoEncontrado);

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Modificación exitosa", "El producto fue actualizado correctamente."));

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al modificar", e.getMessage()));
        }
    }

    //getters y setters
    public String getBusquedaId() { return busquedaId; }
    public void setBusquedaId(String busquedaId) { this.busquedaId = busquedaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
}
