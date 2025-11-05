package ui;

import helper.ProductoHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.desarollo.entity.Producto;
import org.primefaces.PrimeFaces;

import java.io.Serializable;

@Named("altaProdBeanUI") //Nombre utilizado en el xhtml
@SessionScoped
public class AltaProductoBeanUI implements Serializable {

    private String id;
    private String nombre;
    private Integer stock;
    private Double precio;
    private String proveedor;

    private final ProductoHelper productoHelper = new ProductoHelper();

    public void altaProducto() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (nombre == null || nombre.trim().isEmpty())
                throw new Exception("El campo Nombre es obligatorio.");

            if (stock == null || stock < 0)
                throw new Exception("El campo Stock debe ser nuemeros positivos.");

            if (precio == null || precio <= 0)
                throw new Exception("El campo Precio debe ser mayor a 0.");
            if(proveedor == null || proveedor.trim().isEmpty())
                throw new Exception("El campo Proveedor es obligatorio.");

            Producto nuevoProducto = new Producto();
            nuevoProducto.setNombre(nombre.trim());
            nuevoProducto.setStock(stock);
            nuevoProducto.setPrecio(precio);
            nuevoProducto.setProveedor(proveedor.trim());
            nuevoProducto.setTipo("producto");
            nuevoProducto.setIdUsuarioAdmin("ADM1000"); //Admin que despues se cambiara
            productoHelper.altaProducto(nuevoProducto);

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Registro exitoso", "El producto fue agregado correctamente."));

            //PrimeFaces.current().ajax().update("formProductos:tablaProductos formProductos:msgsProducto");
            limpiarCampos();

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al registrar producto", e.getMessage()));
        }
    }




    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    private void limpiarCampos() {
        nombre = "";
        stock = null;
        precio = null;
        proveedor = "";
    }
}
