package mx.avanti.desarollo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import mx.avanti.desarollo.persistence.HibernateUtil;
import mx.desarollo.entity.Cliente;
import mx.desarollo.entity.Clase;

public class AsignacionDAO {

    public void asignarClaseACliente(String idCliente, String idClase) throws Exception {
        //se crea un EntityManager NUEVO por operación
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

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

            //Eliminar la relación en ambos lados
            cliente.getClases().remove(clase);
            clase.getClientes().remove(cliente);

            em.merge(cliente);
            em.merge(clase);

            tx.commit();

            System.out.println("Cliente " + idCliente + " removido de la clase " + idClase);
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new Exception("Error al eliminar asignación: " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

}
