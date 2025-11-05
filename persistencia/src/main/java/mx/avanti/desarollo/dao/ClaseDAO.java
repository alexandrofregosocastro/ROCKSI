package mx.avanti.desarollo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import mx.avanti.desarollo.persistence.AbstractDAO;
import mx.desarollo.entity.Clase;
import mx.desarollo.entity.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClaseDAO extends AbstractDAO<Clase> {
    private final EntityManager em;
    private static boolean contadorInicializado = false;

    public ClaseDAO(EntityManager em) {
        super(Clase.class);
        this.em = em;
        if (!contadorInicializado) {
            sincronizarContador();
            contadorInicializado = true;
        }
    }

    public List<Clase> findAllWithClientes() {
        return execute(em -> {
            //Limpia contexto antes de ejecutar la query
            em.clear();

            List<Clase> result = em.createQuery(
                            "SELECT DISTINCT c FROM Clase c LEFT JOIN FETCH c.clientes",
                            Clase.class
                    )
                    .setHint("jakarta.persistence.cache.storeMode", "REFRESH") //forzar lectura desde la BD
                    .setHint("org.hibernate.cacheable", false)
                    .getResultList();

            return result;
        });
    }


    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public void crear(Clase clase){
        save(clase);
        sincronizarContador();
    }

    /*
    En esta funcion se inicializa el contador para su respectivo ID que empieza con CLI
     */
    private void sincronizarContador() {
        try {
            String ultimoId = em
                    .createQuery("SELECT c.idItem FROM Clase c WHERE c.idItem LIKE 'CLA%' ORDER BY c.idItem DESC", String.class)
                    .setMaxResults(1)
                    .getSingleResult();

            if (ultimoId != null && ultimoId.startsWith("CLA")) {
                int numero = Integer.parseInt(ultimoId.substring(3));
                Clase.setContador(numero + 1);
            }
        } catch (NoResultException e) {
            Clase.setContador(1000);
        } catch (Exception e) {
            Clase.setContador(1000);
            System.err.println("Error al sincronizar: " + e.getMessage());
        }
    }

    //Aqui se genera el nuevo ID
    public String generarNuevoIdClase() {
        return Clase.generarNuevoId();
    }

    public boolean eliminarClase(String idClase){
        EntityTransaction et = null;
        boolean eliminado = false;

        try{
            et = em.getTransaction();
            et.begin();//Se inicializa la transaccion

            Clase clase = em.find(Clase.class, idClase);//Encuentra el id de la clase

            if(clase != null){
                if(!em.contains(clase)){
                    clase = em.merge(clase);
                }
                em.remove(clase);
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
    public Clase buscarClasePorId(String id) {
        try {
            return em.find(Clase.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar la clase por ID", e);
        }
    }

    /*public List<Clase> listarTodasLasClases() {
        return findAll();
    }
     */

    public void actualizarClase(Clase cla) {
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();

            // Iniciar transaccion si no está activa
            if (!tx.isActive()) {
                tx.begin();
            }

            // Actualizar la clase existente
            em.merge(cla);

            // Confirmar los cambios
            tx.commit();

        } catch (Exception e) {
            // Revertir la transaccion si ocurre un error
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }

            throw new RuntimeException("Error al modificar la clase.", e);
        }
    }
}
