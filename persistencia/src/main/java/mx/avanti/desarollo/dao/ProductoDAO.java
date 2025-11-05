package mx.avanti.desarollo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import mx.avanti.desarollo.persistence.AbstractDAO;
import mx.desarollo.entity.Producto;

import java.util.List;

public class ProductoDAO extends AbstractDAO<Producto> {
    private final EntityManager em;
    private static boolean contadorInicializado = false;

    public ProductoDAO(EntityManager em) {
        super(Producto.class);
        this.em = em;
        if (!contadorInicializado) {
            sincronizarContador();
            contadorInicializado = true;
        }
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    //Se agrego el metodo de crear y no se uso save, puesto que al usar save, hibernate lanzaba error aun cuando todo estaba bien
    public void crear(Producto producto) {
        EntityTransaction tx = em.getTransaction();
        try {
            if (!tx.isActive()) {
                tx.begin();
            }
            if (producto.getTipo() == null) {
                producto.setTipo("producto");
            }
            if (producto.getIdUsuarioAdmin() == null) {
                producto.setIdUsuarioAdmin("ADM1000");
            }

            em.persist(producto);
            tx.commit();

            sincronizarContador();

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Error al registrar el producto", e);
        }
    }


    /*
    En esta funcion se inicializa el contador para su respectivo ID que empieza con CLI
     */
    private void sincronizarContador() {
        try {
            String ultimoId = em
                    .createQuery("SELECT p.idItem FROM Producto p WHERE p.idItem LIKE 'PR%' ORDER BY p.idItem DESC", String.class)
                    .setMaxResults(1)
                    .getSingleResult();

            if (ultimoId != null && ultimoId.startsWith("PR")) {
                int numero = Integer.parseInt(ultimoId.substring(2)); // quitar "PR"
                Producto.setContador(numero + 1);
            }
        } catch (NoResultException e) {
            Producto.setContador(1000);
        } catch (Exception e) {
            Producto.setContador(1000);
            System.err.println("Error al sincronizar la id: " + e.getMessage());
        }
    }

    //Aqui se genera el nuevo ID
    public String generarNuevoIdProducto() {
        return Producto.generarNuevoId();
    }

    /*
   Con esta funcion se elimina un producto por su ID
    */
    public boolean eliminarProducto(String idProducto) {
        EntityTransaction et = null;
        boolean eliminado = false;

        try{
            et = em.getTransaction();
            et.begin();//Se inicializa la transaccion

            Producto producto = em.find(Producto.class, idProducto);//Encuentra el id del producto

            if(producto != null){
                if(!em.contains(producto)){
                    producto = em.merge(producto);
                }
                em.remove(producto);
                eliminado = true;//Se confirma la eliminacion
            }
            et.commit();//Se manda lo realizado
            return eliminado;

        } catch (Exception e){
            if(et != null && et.isActive()) et.rollback();
            e.printStackTrace();
        }
        return eliminado;
    }

    /*
   Con esta funcion se obtiene un producto por su ID
    */
    public Producto buscarProductoPorId(String id) {
        try {
            return em.find(Producto.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el producto por ID...", e);
        }
    }

    public void reducirStock(String idProducto) {
        EntityTransaction tx = em.getTransaction();
        try {
            if (!tx.isActive()) {
                tx.begin();
            }

            Producto producto = em.find(Producto.class, idProducto);
            if (producto == null) {
                throw new RuntimeException("No se encontró el producto con ID: " + idProducto);
            }

            if (producto.getStock() <= 0) {
                throw new RuntimeException("El producto " + producto.getNombre() + " no tiene stock disponible.");
            }

            producto.setStock(producto.getStock() - 1);
            em.merge(producto);
            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al reducir el stock del producto " + idProducto, e);
        }
    }


    /*
   Con esta funcion se listan todos los productos registrados
    */
    public List<Producto> listarTodosLosProductos() {
        return findAll();
    }

    public void actualizarProducto(Producto producto) {
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();

            // Iniciar transaccion si no está activa
            if (!tx.isActive()) {
                tx.begin();
            }

            // Actualizar el producto existente
            em.merge(producto);

            // Confirmar los cambios
            tx.commit();

        } catch (Exception e) {
            // Revertir la transaccion si ocurre un error
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }

            throw new RuntimeException("Error al modificar el producto.", e);
        }
    }
}
