package helper;

import mx.desarollo.entity.Producto;
import mx.desarollo.integration.ServiceFacadeLocator;
import java.util.List;
import java.io.Serializable;

public class ProductoHelper implements Serializable {
    public void altaProducto(Producto producto) throws Exception {
        try{
            ServiceFacadeLocator.getInstanceProductoFacade().altaProducto(producto);
        }catch (Exception e){
            throw new Exception("Error al realizar la alta del producto" + e.getMessage());
        }
    }

    /**
     * Metodo para eliminar un producto por su ID que llamara a la instancia de ProductoFacade
     * @Throws Si la base de datos rechaza la peticion o no se encuentra un producto con el ID
     * @Param id de tipo String
     * @return Una respuesta de tipo boolean
     */
    public boolean eliminarProductos(String idProducto) throws Exception {
        return ServiceFacadeLocator.getInstanceProductoFacade().eliminarProducto(idProducto);
    }

    public void reducirStock(String idProducto) throws Exception {
        ServiceFacadeLocator.getInstanceProductoFacade().reducirStock(idProducto);
    }

    public void modificarProducto(Producto producto) throws Exception {
        ServiceFacadeLocator.getInstanceProductoFacade().actualizarProducto(producto);
    }

    /**
     * Metodo para obtener un producto por su ID que llamara a la instancia de ProductoFacade
     * @Throws Si la base de datos rechaza la peticion o no se encuentra un producto con el ID
     * @Param id de tipo String
     * @return Un objeto de tipo Producto
     */
    public Producto obtenerProducto(String id) {
        return ServiceFacadeLocator.getInstanceProductoFacade().obtenerProductoPorId(id);

    }

    /**
     * Metodo para listar de todos los productos que llamara a la instancia de ProductoFacade
     * @Throws Si la base de datos rechaza la peticion
     * @return Una lista de productos
     */
    public List<Producto> listarProductos() throws Exception {
        return ServiceFacadeLocator.getInstanceProductoFacade().listarProductos();
    }
}
