package mx.desarollo.facade;

import mx.desarollo.delegate.UsuarioRDelegate;
import mx.desarollo.entity.Usuariorecepcionista;

import java.util.List;

public class UsuarioRFacade {
    private final UsuarioRDelegate usuarioRDelegate = new UsuarioRDelegate();

    /**
     * Metodo para registrar un usuario recepcionista que llamara a la instancia de UsuarioRDelegate
     * @Throws Si la base de datos rechaza el registro
     * @Params Objeto de tipo Usuariorecepcionista
     * @return void
     */
    public void registrarUsuarioRecepcionista(Usuariorecepcionista ur) throws Exception {
        usuarioRDelegate.registrarUsuarioR(ur);
    }

    public Usuariorecepcionista obtenerUsuarioRPorId(String id) {
        return usuarioRDelegate.obtenerUR(id);
    }

    /**
     * Metodo para listar todos los productos registrados que llamara a la instancia de ProductoDelegate
     * @Throws Si la base de datos rechaza la peticion de busqueda por ID
     * @Params Objeto de tipo String id
     * @return Una lista de productos
     */
    public List<Usuariorecepcionista> listarUsuarioR() {
        return usuarioRDelegate.listarUR();
    }

    public void modificarUsuarioR(Usuariorecepcionista id) throws Exception {
        usuarioRDelegate.modificarUsuarioR(id);
    }

    public boolean bajaUsuarioR(String id) throws Exception {
        return usuarioRDelegate.bajaUsuarioR(id);
    }

}
