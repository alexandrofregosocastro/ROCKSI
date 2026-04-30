package mx.desarollo.delegate;

import mx.avanti.desarollo.dao.UsuarioADao;
import mx.avanti.desarollo.integration.ServiceLocator;
import mx.desarollo.entity.Cliente;
import mx.desarollo.entity.Usuarioadministrador;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;
import java.util.List;

public class UsuarioADelegate {
    private final UsuarioADao usuarioADao;

    public UsuarioADelegate() {
        // Asegúrate de tener getInstanceUADAO() en ServiceLocator
        this.usuarioADao = ServiceLocator.getInstanceUADAO();
    }

    /**
     * Metodo para registrar un usuario administrador que llamara a la instancia de UsuarioADAO
     * @Throws Si la base de datos rechaza el registro
     * @params Un objeto de tipo Usuarioadministrador
     * @return void
     */
    public void registrarUsuarioA(Usuarioadministrador ua) throws Exception {
        //validaciones
        if (ua.getNombreCompleto() == null || ua.getNombreCompleto().trim().isEmpty()) {
            throw new Exception("El nombre no puede estar vacio.");
        }
        if (ua.getCorreo() == null || ua.getCorreo().trim().isEmpty()) {
            throw new Exception("El correo no puede estar vacio.");
        }

        if (ua.getContrasena() == null || ua.getContrasena().trim().isEmpty()) {
            throw new Exception("Se debe asignar una contraseña.");
        }

        if (!ua.getNombreCompleto().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new Exception("El nombre solo puede contener letras y espacios.");
        }

        // Encriptacion (Hashing)
        // Generamos un hash a partir de la contraseña
        String passwordPlana = ua.getContrasena();
        String passwordHasheada = BCrypt.hashpw(passwordPlana, BCrypt.gensalt());

        // Sustituimos la contraseña texto por la encriptada antes de guardar
        ua.setContrasena(passwordHasheada);

        ua.setEstatus(1);

        usuarioADao.crearUsuarioA(ua);
    }

    public void modificarUsuarioA(Usuarioadministrador ua) throws Exception {
        // validaciones basicas
        if (ua == null || ua.getIdUsuarioadmin() == null || ua.getIdUsuarioadmin().trim().isEmpty()) {
            throw new Exception("No se puede modificar sin un ID válido.");
        }
        if (ua.getNombreCompleto() == null || ua.getNombreCompleto().trim().isEmpty()) {
            throw new Exception("El nombre completo es obligatorio.");
        }
        if (ua.getCorreo() == null || !ua.getCorreo().contains("@")) {
            throw new Exception("Ingrese un correo válido.");
        }
        if (ua.getContrasena() == null || ua.getContrasena().trim().isEmpty()) {
            throw new Exception("La contraseña no puede estar vacía.");
        }

        // Encriptacion (Hashing)
        // Generamos un hash a partir de la contraseña
        String passwordPlana = ua.getContrasena();
        String passwordHasheada = BCrypt.hashpw(passwordPlana, BCrypt.gensalt());

        // Sustituimos la contraseña texto por la encriptada antes de guardar
        ua.setContrasena(passwordHasheada);

        usuarioADao.actualizar(ua);
    }

    public boolean bajaUsuarioA(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("El ID es necesario para dar la baja.");
        }

        Usuarioadministrador ua = usuarioADao.buscarADMPorId(id);
        if (ua == null) {
            throw new Exception("El usuario con ID " + id + " no existe.");
        }

        if (ua.getEstatus() == 0) {
            throw new Exception("El usuario ya se encuentra dado de baja.");
        }

        return usuarioADao.baja(id);
    }

    public Usuarioadministrador obtenerUA(String id) {
        Usuarioadministrador ua = usuarioADao.buscarADMPorId(id);
        if (ua != null) {
            ua.setContrasena(""); // Limpiamos el hash antes de enviarlo a la vista
        }
        return ua;
    }


    public Usuarioadministrador obtenerUsuarioAPorId(String id) {
        return usuarioADao.buscarADMPorId(id);
    }

    public List<Usuarioadministrador> listarUA() {
        return usuarioADao.listarActivos();
    }
}