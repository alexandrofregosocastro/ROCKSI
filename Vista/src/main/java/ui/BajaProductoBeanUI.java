package ui;

import helper.ProductoHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

@Named("bajaProductoBeanUI")
@SessionScoped
public class BajaProductoBeanUI implements Serializable {
    private String idProducto;
    private final ProductoHelper productoHelper = new ProductoHelper();

    public void prepararEliminacion(String id) {
        this.idProducto = id;
    }

    public void eliminarProducto(){
        try{
            boolean eliminado = productoHelper.eliminarProductos(idProducto);//Se usa boolean para verificar si se elimino o no
            FacesContext fc = FacesContext.getCurrentInstance();

            if(eliminado){
                ProductoBeanUI productoBeanUI = (ProductoBeanUI) fc.getApplication()
                        .getELResolver().getValue(fc.getELContext(), null, "productoBeanUI");
                if (productoBeanUI != null) productoBeanUI.cargarProductos();

                fc.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Baja Exitosa", "Producto eliminado correctamente."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "No encontrado", "No existe ningun producto con ese ID."));
            }


        }catch(Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar", e.getMessage()));
        }
    }

    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }


}

