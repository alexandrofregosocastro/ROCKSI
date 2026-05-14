package mx.avanti.desarollo.dao;

import jakarta.persistence.EntityManager;
import mx.avanti.desarollo.persistence.AbstractDAO;
import mx.desarollo.entity.MovimientoCaja;

public class MovimientoCajaDAO extends AbstractDAO<MovimientoCaja> {

    private final EntityManager em;

    public MovimientoCajaDAO(EntityManager em) {
        super(MovimientoCaja.class);
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Guarda el registro en la tabla movimiento_caja
     */
    public void registrarMovimiento(MovimientoCaja movimiento) throws Exception {
        EntityManager em = getEntityManager();
        try {
            // Validamos que la conexión no esté cerrada ni ocupada antes de iniciar
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            em.persist(movimiento);
            em.getTransaction().commit();

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Cancelamos si hay error
            }
            throw new Exception("Error en la base de datos al registrar el movimiento: " + e.getMessage());
        }
    }

    /**
     * Busca un registro específico por su ID numérico
     */
    public MovimientoCaja obtenerMovimientoPorId(Integer idMovimiento) throws Exception {
        EntityManager em = getEntityManager();
        try {
            return em.find(MovimientoCaja.class, idMovimiento);
        } catch (Exception e) {
            throw new Exception("Error al consultar el movimiento en la base de datos: " + e.getMessage());
        }
    }

    /**
     * Método para conocer si existe una apertura en el dia
     */
    public boolean existeAperturaHoy() throws Exception {
        EntityManager em = getEntityManager();
        try {
            // Obtenemos el inicio y fin del día actual
            java.time.LocalDateTime inicioDia = java.time.LocalDate.now().atStartOfDay();
            java.time.LocalDateTime finDia = java.time.LocalDate.now().atTime(23, 59, 59);

            // Contamos si ya existe una apertura anterior
            Long conteo = em.createQuery(
                            "SELECT COUNT(m) FROM MovimientoCaja m WHERE m.tipoMovimiento = 'APERTURA' AND m.fechaHora >= :inicio AND m.fechaHora <= :fin", Long.class)
                    .setParameter("inicio", inicioDia)
                    .setParameter("fin", finDia)
                    .getSingleResult();

            return conteo > 0; // Retorna true si ya hay una apertura, false si no
        } catch (Exception e) {
            throw new Exception("Error al verificar la apertura de hoy: " + e.getMessage());
        }
    }

    /**
     * Método para obtener el registro exacto de hoy
     */
    public MovimientoCaja obtenerAperturaHoy() throws Exception {
        EntityManager em = getEntityManager();
        try {
            java.time.LocalDateTime inicioDia = java.time.LocalDate.now().atStartOfDay();
            java.time.LocalDateTime finDia = java.time.LocalDate.now().atTime(23, 59, 59);

            return em.createQuery(
                            "SELECT m FROM MovimientoCaja m WHERE m.tipoMovimiento = 'APERTURA' AND m.fechaHora >= :inicio AND m.fechaHora <= :fin", MovimientoCaja.class)
                    .setParameter("inicio", inicioDia)
                    .setParameter("fin", finDia)
                    .setMaxResults(1) // Solo el de hoy
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new Exception("Error al obtener la apertura de hoy: " + e.getMessage());
        }
    }

    /**
     * Método para hacer una actulizacion de un movimiento en caja
     */
    public void actualizarMovimiento(MovimientoCaja movimiento) throws Exception {
        EntityManager em = getEntityManager();
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }
            em.merge(movimiento);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Error al actualizar el movimiento: " + e.getMessage());
        }
    }
}