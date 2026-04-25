package mx.avanti.desarollo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import mx.avanti.desarollo.persistence.AbstractDAO;
import mx.desarollo.entity.Membresia;

import java.util.List;

public class MembresiaDAO extends AbstractDAO<Membresia> {
    private final EntityManager em;
    private static boolean contadorInicializado = false;

    public MembresiaDAO(EntityManager em) {
        super(Membresia.class);
        this.em = em;
        if (!contadorInicializado) {
            sincronizarContador();
            contadorInicializado = true;
        }
    }

    public List<Membresia> findAllWithMembresia() {
        return execute(em -> {
            //Limpia contexto antes de ejecutar la query
            em.clear();

            List<Membresia> result = em.createQuery(
                            "SELECT DISTINCT m FROM Membresia m LEFT JOIN FETCH m.idItem",
                            Membresia.class
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

    public void crear(Membresia membresia){
        save(membresia);
        sincronizarContador();
    }

    /*
    En esta funcion se inicializa el contador para su respectivo ID que empieza con M
     */
    private void sincronizarContador() {
        try {
            String ultimoId = em
                    .createQuery("SELECT m.idItem FROM Membresia m WHERE m.idItem LIKE 'M%' ORDER BY m.idItem DESC", String.class)
                    .setMaxResults(1)
                    .getSingleResult();

            if (ultimoId != null && ultimoId.startsWith("M")) {
                int numero = Integer.parseInt(ultimoId.substring(1));
                Membresia.setContador(numero + 1);
                System.out.println("Contador de membresias sincronizado: siguiente M" + (numero + 1));
            }
        } catch (NoResultException e) {
            Membresia.setContador(1000);
            System.out.println("ℹNo hay membresias registradas. Contador iniciado en M1000.");
        } catch (Exception e) {
            Membresia.setContador(1000);
            System.err.println("Error al sincronizar el contador de Membresias, se mantiene en M1000: " + e.getMessage());
        }
    }

    //Aqui se genera el nuevo ID
    public String generarNuevoIdMembresia() {
        return Membresia.generarNuevoId();
    }

    public boolean eliminarMembresia(String idMembresia){
        EntityTransaction et = null;
        boolean eliminado = false;

        try{
            et = em.getTransaction();
            et.begin();//Se inicializa la transaccion

            Membresia membresia = em.find(Membresia.class, idMembresia);//Encuentra el id de la membresia

            if(membresia != null){
                if(!em.contains(membresia)){
                    membresia = em.merge(membresia);
                }
                em.remove(membresia);
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

    public Membresia buscarMembresiaPorId(String idMembresia) {
        try {
            return em.find(Membresia.class, idMembresia);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar la Membresia por ID", e);
        }
    }

    public void modificarMembresia(Membresia membresia) {
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();

            // Iniciar transaccion si no está activa
            if (!tx.isActive()) {
                tx.begin();
            }

            // Actualizar la membresia existente
            em.merge(membresia);

            // Confirmar los cambios
            tx.commit();

        } catch (Exception e) {
            // Revertir la transaccion si ocurre un error
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }

            throw new RuntimeException("Error al modificar la membresia.", e);
        }


    }

    /**
     * Obtiene la membresia mas reciente de un cliente, filtrando por un tipo especifico ("membresia" o "clase").
     * @param idCliente El ID del cliente.
     * @param tipo El tipo de membresía a buscar.
     * @return La membresia más reciente de ese tipo, o null si no se encuentra.
     */
    public Membresia obtenerMembresiaPorCliente(String idCliente, String tipo) {
        EntityManager em = getEntityManager();
        try {
            System.out.println("=== DEBUG CONSULTA MEMBRESiA ===");
            System.out.println("Buscando para Cliente ID: [" + idCliente + "] | Tipo que buscamos: [" + tipo + "]");

            Membresia encontrada = em.createQuery(
                            "SELECT m FROM Membresia m " +
                                    "WHERE m.idCliente.idCliente = :idCliente " +
                                    "AND LOWER(TRIM(m.tipo)) = LOWER(TRIM(:tipoMembresia)) " +
                                    "ORDER BY m.fechaVencimiento DESC",
                            Membresia.class)
                    .setParameter("idCliente", idCliente)
                    .setParameter("tipoMembresia", tipo)
                    .setMaxResults(1)
                    .getSingleResult();

            System.out.println("Si encontro una membresia");
            System.out.println("ID del Item encontrado: " + encontrada.getIdItem());
            System.out.println("Vence el: " + encontrada.getFechaVencimiento());
            System.out.println("================================");

            return encontrada;

        } catch (jakarta.persistence.NoResultException e) {
            System.out.println("No tiene membresia previa. Es cliente limpio.");
            System.out.println("================================");
            return null;
        }
    }


}
