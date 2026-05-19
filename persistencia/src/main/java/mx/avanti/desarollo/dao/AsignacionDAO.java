package mx.avanti.desarollo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import mx.avanti.desarollo.persistence.HibernateUtil;
import mx.desarollo.entity.Cliente;
import mx.desarollo.entity.Clase;
import mx.desarollo.entity.Membresia;

import java.time.LocalDate;

public class AsignacionDAO {

    public void asignarClaseACliente(String idCliente, String idClase) throws Exception {
        //se crea un EntityManager NUEVO por operación
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();
///
            Cliente cliente = em.find(Cliente.class, idCliente);
            Clase clase = em.find(Clase.class, idClase);

            if (cliente == null) {
                throw new Exception("No existe cliente con ID " + idCliente);
            }
            if (clase == null) {
                throw new Exception("No existe clase con ID " + idClase);
            }

            //Validación de cupo
            int inscritos = clase.getClientes().size();
            int cupoMax = clase.getCupoMaximo();

            if (inscritos >= cupoMax) {
                throw new Exception("No se puede asignar el cliente. La clase '" + clase.getNombre() +
                        "' ha alcanzado su cupo máximo de " + cupoMax + " participantes.");
            }

            //evitar duplicados
            if (cliente.getClases().contains(clase)) {
                throw new Exception("El cliente ya está inscrito en la clase '" + clase.getNombre() + "'.");
            }

            //asignar clase y cliente en ambos lados
            cliente.getClases().add(clase);
            clase.getClientes().add(cliente);

            //guardar cambios
            em.merge(cliente);
            em.merge(clase);

            tx.commit();

            System.out.println("Cliente " + cliente.getIdCliente() + " asignado a la clase " + clase.getNombre());

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new Exception("Error asignando clase a cliente: " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void eliminarAsignacionClienteClase(String idCliente, String idClase) throws Exception {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            Cliente cliente = em.find(Cliente.class, idCliente);
            Clase clase = em.find(Clase.class, idClase);

            if (cliente == null) {
                throw new Exception("No existe el cliente con ID " + idCliente);
            }
            if (clase == null) {
                throw new Exception("No existe la clase con ID " + idClase);
            }

            if (!cliente.getClases().contains(clase)) {
                throw new Exception("El cliente no está inscrito en esta clase.");
            }

            // 1. Eliminar la relación en ambos lados (Tabla intermedia)
            cliente.getClases().remove(clase);
            clase.getClientes().remove(cliente);

            em.merge(cliente);
            em.merge(clase);

            // 2. Buscar y "cancelar" la membresía de tipo 'clase' en lugar de borrarla
            try {
                Membresia membresiaClase = em.createQuery(
                                "SELECT m FROM Membresia m WHERE m.idCliente.idCliente = :idCliente AND m.tipo = 'clase'", Membresia.class)
                        .setParameter("idCliente", idCliente)
                        .getSingleResult();

                if (membresiaClase != null) {
                    // En lugar de borrarla con remove(), la vencemos asignándole la fecha de ayer
                    membresiaClase.setFechaVencimiento(LocalDate.now().minusDays(1));
                    em.merge(membresiaClase); // Actualizamos el registro

                    System.out.println("Membresía de clase expirada (cancelada) para el cliente: " + idCliente);
                }
            } catch (jakarta.persistence.NoResultException nre) {
                System.out.println("El cliente no tenía una membresía de tipo clase activa.");
            }

            tx.commit();

            System.out.println("Cliente " + idCliente + " removido de la clase " + idClase);
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new Exception("Error al eliminar asignación: " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public boolean verificarClaseAsignadaACliente(String idCliente, String idClase) throws Exception {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        boolean asignada = false;

        try {
            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM Cliente c JOIN c.clases cl " +
                                    "WHERE c.idCliente = :idCliente AND cl.idItem = :idClase",
                            Long.class)
                    .setParameter("idCliente", idCliente)
                    .setParameter("idClase", idClase)
                    .getSingleResult();

            asignada = count != null && count > 0;
        } catch (Exception e) {
            throw new Exception("Error al verificar si el cliente tiene la clase asignada: " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }

        return asignada;
    }

}
