package mx.avanti.desarollo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import mx.avanti.desarollo.persistence.AbstractDAO;
import mx.desarollo.entity.Cliente;
import mx.desarollo.entity.Membresia;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClienteDAO extends AbstractDAO<Cliente> {

    private final EntityManager entityManager;

    public ClienteDAO(EntityManager em) {
        super(Cliente.class);
        this.entityManager = em;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void crear(Cliente cliente) {
        EntityTransaction tx = null;
        try {
            cliente.setEstatus(1);

            if (cliente.getIdCliente() == null || cliente.getIdCliente().isEmpty()) {
                cliente.setIdCliente(generarSiguienteIdCliente());
            }

            tx = entityManager.getTransaction();
            if (!tx.isActive()) {
                tx.begin();
            }

            entityManager.persist(cliente);
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear el cliente", e);
        }
    }

    /*
    En esta funcion se inicializa el contador para su respectivo ID que empieza con CLI
     */
    public String generarSiguienteIdCliente() {
        try {
            // Consulta nativa que corta "CLI", convierte el resto a número y busca el máximo absoluto
            String sql = "SELECT MAX(CAST(SUBSTRING(ID_Cliente, 4) AS UNSIGNED)) FROM cliente";
            Object resultado = entityManager.createNativeQuery(sql).getSingleResult();

            if (resultado != null) {
                int maxNumero = ((Number) resultado).intValue();
                return "CLI" + (maxNumero + 1);
            } else {
                return "CLI1000"; // Si la tabla está vacía inicia aquí
            }
        } catch (Exception e) {
            System.err.println("Error al generar ID de Cliente: " + e.getMessage());
            return "CLI" + System.currentTimeMillis(); // Fallback de emergencia
        }
    }

    public boolean eliminarCliente(String idCliente) {
        EntityTransaction et = null;
        boolean eliminado = false;

        try {
            et = entityManager.getTransaction();
            et.begin();

            Cliente cliente = entityManager.find(Cliente.class, idCliente);

            if (cliente != null) {
                // 1. Soft Delete del cliente
                cliente.setEstatus(0);
                entityManager.merge(cliente);

                // Cancelar membresía de tipo 'membresia'
                try {
                    // Buscamos la membresía principal ligada a este cliente
                    Membresia membresiaPrincipal = entityManager.createQuery(
                                    "SELECT m FROM Membresia m WHERE m.idCliente.idCliente = :idCliente AND m.tipo = 'membresia'", Membresia.class)
                            .setParameter("idCliente", idCliente)
                            .getSingleResult();

                    if (membresiaPrincipal != null) {
                        // Le ponemos fecha de vencimiento de ayer para invalidarla
                        // Sin borrar el registro para mantener la integridad con el historial de pagos
                        membresiaPrincipal.setFechaVencimiento(LocalDate.now().minusDays(1));
                        entityManager.merge(membresiaPrincipal);
                    }
                } catch (Exception e) {




                    // Si el cliente no tiene membresía activa, el flujo continúa normalmente
                    System.out.println("No se encontró membresía de tipo 'membresia' para expirar.");
                }

                eliminado = true;
            }

            et.commit();
        } catch (Exception e) {
            if (et != null && et.isActive()) et.rollback();
            e.printStackTrace();
        }

        return eliminado;
    }

    public Cliente buscarPorId(String id) {
        try {
            return entityManager.find(Cliente.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar cliente por ID", e);
        }
    }

    //Aqui solo se despliegan los activos
    public List<Cliente> listarTodos() {
        return execute(em -> {
            em.clear();
            return em.createQuery("SELECT c FROM Cliente c WHERE c.estatus = 1", Cliente.class)
                    .getResultList();
        });
    }

    public void actualizar(Cliente cliente) {
        EntityTransaction tx = null;

        try {
            tx = entityManager.getTransaction();

            // Iniciar transaccion si no está activa
            if (!tx.isActive()) {
                tx.begin();
            }

            // Actualizar el cliente existente
            entityManager.merge(cliente);

            // Confirmar los cambios
            tx.commit();

        } catch (Exception e) {
            // Revertir la transaccion si ocurre un error
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }

            throw new RuntimeException("Error al modificar el cliente.", e);
        }
    }

    public List<Cliente> findByFechaRegistroBetween(java.util.Date inicioMes, java.util.Date finMes) {
        return execute(em -> {
            em.clear();
            return em.createQuery(
                            "SELECT c FROM Cliente c " +
                                    "WHERE c.fechaRegistro >= :inicio AND c.fechaRegistro <= :fin", Cliente.class)
                    .setParameter("inicio", inicioMes, jakarta.persistence.TemporalType.DATE)
                    .setParameter("fin", finMes, jakarta.persistence.TemporalType.DATE)
                    .setHint("jakarta.persistence.cache.storeMode", "REFRESH")
                    .getResultList();
        });
    }
}
