package mx.desarollo.facade;

import jakarta.persistence.EntityManager;
import mx.avanti.desarollo.dao.UsuarioADao;
import mx.avanti.desarollo.persistence.HibernateUtil;
import mx.desarollo.delegate.UsuarioADelegate;
import mx.desarollo.entity.Cliente;
import mx.desarollo.entity.Usuarioadministrador;

import java.util.List;

public class UsuarioAFacade {
    private final UsuarioADelegate delegate = new UsuarioADelegate();

    private EntityManager getEntityManager() {
        return HibernateUtil.getEntityManager();
    }

    /**
     * Metodo para registrar un usuario administrador que llamara a la instancia de UsuarioADelegate
     * @Throws Si la base de datos rechaza el registro
     * @Params Objeto de tipo Usuarioadministrador
     * @return void
     */
    public void registrarUsuarioAdministrador(Usuarioadministrador ua) throws Exception {
        delegate.registrarUsuarioA(ua);
    }

    public void modificarUsuarioA(Usuarioadministrador ua) throws Exception {
        delegate.modificarUsuarioA(ua);
    }

    public boolean bajaUsuarioA(String id) throws Exception {
        return delegate.bajaUsuarioA(id);
    }

    public List<Usuarioadministrador> listarUA() {
        return delegate.listarUA();
    }
    public Usuarioadministrador obtenerUsuarioAPorId(String id) { return delegate.obtenerUsuarioAPorId(id); }
    public Usuarioadministrador obtenerUA(String id) { return delegate.obtenerUA(id); }

}