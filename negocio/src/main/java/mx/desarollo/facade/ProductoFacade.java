package mx.desarollo.facade;

import mx.desarollo.delegate.ProductoDelegate;
import mx.desarollo.entity.Producto;

import java.util.List;

public class ProductoFacade {
    private final ProductoDelegate productoDelegate = new ProductoDelegate();

    public void altaProducto(Producto producto) throws Exception
    {
        try{
            productoDelegate.altaProducto(producto);
        } catch (Exception e){
            throw new Exception("Error al realizar la alta: " + e.getMessage());
        }
    }

    /**
     * Metodo para eliminar un producto por su ID que llamara a la instancia de ProductoDelegate
     * @Throws Si la base de datos rechaza la peticion o no se encuentra un producto con el ID
     * @Params Objeto de tipo String id
     * @return Una respuesta de tipo boolean
     */
    public boolean eliminarProducto(String idProducto) throws Exception {
        try{
            return productoDelegate.eliminarProducto(idProducto);
        } catch(Exception e){
            throw new Exception("Error al eliminar el producto: " + e.getMessage());
        }
    }

    public void reducirStock(String idProducto) throws Exception {
        productoDelegate.reducirStock(idProducto);
    }

    public void actualizarProducto(Producto producto) throws Exception {
        productoDelegate.actualizarProducto(producto);
    }

    /**
     * Metodo para obtener un producto por su ID que llamara a la instancia de ProductoDelegate
     * @Throws Si la base de datos rechaza la peticion o no se encuentra un producto con el ID
     * @Params Objeto de tipo String id
     * @return Un objeto de tipo Producto
     */
    public Producto obtenerProductoPorId(String id) {
        return productoDelegate.obtenerProducto(id);
    }

    /**
     * Metodo para listar todos los productos registrados que llamara a la instancia de ProductoDelegate
     * @Throws Si la base de datos rechaza la peticion de busqueda por ID
     * @Params Objeto de tipo String id
     * @return Una lista de productos
     */
    public List<Producto> listarProductos() {
        return productoDelegate.listarProductos();
    }
}
