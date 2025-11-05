package mx.avanti.desarollo.dao;

import jakarta.persistence.EntityManager;
import mx.avanti.desarollo.persistence.AbstractDAO;
import mx.desarollo.entity.Cliente;

import java.util.List;
import java.util.Optional;

public class ClienteDAO extends AbstractDAO<Cliente> {

    private final EntityManager entityManager;

    public ClienteDAO(EntityManager em) {
        super(Cliente.class);
        this.entityManager = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public void crear(Cliente cliente) {
        save(cliente);
    }

    /*public Cliente buscarPorId(int id) {
        Optional<Cliente> opt = find(id);
        return opt.orElse(null);
    }

    public List<Cliente> listarTodos() {
        return findAll();
    }

    public void eliminar(int id) {
        Optional<Cliente> opt = find(id);
        opt.ifPresent(this::delete);
    }
    public void actualizar(Cliente cliente) {
        update(cliente);
    }

    public Cliente buscarPorTelefono(String Telefono) {
        List<Cliente> resultados = entityManager
                .createQuery("SELECT c FROM Cliente c WHERE c.telefono = :Telefono", Cliente.class)
                .setParameter("Telefono", Telefono)
                .getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }*/
}
