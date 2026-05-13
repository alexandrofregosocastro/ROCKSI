package mx.desarollo.delegate;

import mx.avanti.desarollo.dao.UsuarioRDAO;
import mx.avanti.desarollo.integration.ServiceLocator;
import mx.desarollo.entity.Usuarioadministrador;
import mx.desarollo.entity.Usuariorecepcionista;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UsuarioRDelegate {
    private final UsuarioRDAO UsuarioRDAO;

    public UsuarioRDelegate() {
        this.UsuarioRDAO = ServiceLocator.getInstanceURDAO();
    }

    /**
     * Metodo para registrar un usuario recepcionista que llamara a la instancia de UsuarioRDAO
     * @Throws Si la base de datos rechaza el registro o alguna variable es null o esta vacia
     * @params Un objeto de tipo Usuariorecepcionista
     * @return void
     */
    public void registrarUsuarioR(Usuariorecepcionista ur) throws Exception {
        //validaciones
        if (ur.getNombreCompleto() == null || ur.getNombreCompleto().trim().isEmpty()) {
            throw new Exception("El nombre no puede estar vacio.");
        }
        if (ur.getCorreo() == null || ur.getCorreo().trim().isEmpty()) {
            throw new Exception("El correo no puede estar vacio.");
        }

        if (ur.getContrasena() == null || ur.getContrasena().trim().isEmpty()) {
            throw new Exception("Se debe asignar una contraseña.");
        }

        if (!ur.getNombreCompleto().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new Exception("El nombre solo puede contener letras y espacios.");
        }

        // Encriptacion (Hashing)
        // Generamos un hash a partir de la contraseña
        String passwordPlana = ur.getContrasena();
        String passwordHasheada = BCrypt.hashpw(passwordPlana, BCrypt.gensalt());

        // Sustituimos la contraseña texto por la encriptada antes de guardar
        ur.setContrasena(passwordHasheada);

        ur.setEstatus(1);

        UsuarioRDAO.crearUsuarioR(ur);
    }

    public Usuariorecepcionista obtenerUR(String id) {
        try {
            if (id == null) return null;
            id = id.trim();
            if (id.isEmpty()) return null;

            Usuariorecepcionista ur = UsuarioRDAO.buscarURPorId(id);
            if (ur != null) {
                ur.setContrasena(""); // Limpiamos el hash antes de enviarlo a la vista
            }
            return ur;

        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo al usuario recepcionista con id=" + id, e);
        }
    }

    public void modificarUsuarioR(Usuariorecepcionista ur) throws Exception {
        // validaciones basicas
        if (ur == null || ur.getIdUsuariorecep() == null || ur.getIdUsuariorecep().trim().isEmpty()) {
            throw new Exception("No se puede modificar un usuario sin identificación válida.");
        }
        if (ur.getNombreCompleto() == null || ur.getNombreCompleto().trim().isEmpty()) {
            throw new Exception("El nombre completo es obligatorio.");
        }
        if (ur.getCorreo() == null || !ur.getCorreo().contains("@")) {
            throw new Exception("Ingrese un correo válido.");
        }

        // logica para editar la contrasena
        if (ur.getContrasena() == null || ur.getContrasena().trim().isEmpty()) {
            // Si viene vacía, es que no se edita la contrasena
            // conservamos el hash antiguo
            Usuariorecepcionista usuarioViejo = UsuarioRDAO.buscarURPorId(ur.getIdUsuariorecep());
            ur.setContrasena(usuarioViejo.getContrasena());
        } else {
            // Si no viene vacia significa que se modifico
            String passwordPlana = ur.getContrasena();
            String passwordHasheada = BCrypt.hashpw(passwordPlana, BCrypt.gensalt());
            ur.setContrasena(passwordHasheada);
        }

        UsuarioRDAO.actualizar(ur);
    }

    public boolean bajaUsuarioR(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("El ID del usuario es necesario para dar la baja.");
        }

        // se valida si existe el usuario
        Usuariorecepcionista ur = UsuarioRDAO.buscarURPorId(id);
        if (ur == null) {
            throw new Exception("El usuario con ID " + id + " no existe.");
        }

        if (ur.getEstatus() == 0) {
            throw new Exception("El usuario ya se encuentra dado de baja.");
        }

        return UsuarioRDAO.baja(id);
    }

    public List<Usuariorecepcionista> listarUR() {
        return UsuarioRDAO.findAllWithUsuarioR();
    }

    public Usuariorecepcionista obtenerURPorId(String id) {
        return UsuarioRDAO.buscarURPorId(id);
    }
}
