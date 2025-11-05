import mx.avanti.desarollo.dao.ProductoDAO;
import mx.avanti.desarollo.persistence.HibernateUtil;
import mx.desarollo.entity.Producto;

import java.util.List;

public class testDAO {

    public static void main(String[] args) {
        try {
            // Crear DAO pasando el EntityManager
            ProductoDAO productoDAO = new ProductoDAO(HibernateUtil.getEntityManager());

            // Llamar al método que obtiene todos los productos
            List<Producto> listaProductos = productoDAO.findAll();

            // Verificar si hay productos
            if (listaProductos == null || listaProductos.isEmpty()) {
                System.out.println("⚠️ No se encontraron productos en la base de datos.");
            } else {
                // Imprimir los productos encontrados
                for (Producto p : listaProductos) {
                    System.out.println("Producto: " + p.getNombre() + " | ID: " + p.getIdItem());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerrar el EntityManager al terminar
            HibernateUtil.close();
        }
    }

}
