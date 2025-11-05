package mx.desarollo.delegate;

import mx.avanti.desarollo.dao.ClienteDAO;
import mx.avanti.desarollo.integration.ServiceLocator;
import mx.desarollo.entity.Cliente;
import mx.desarollo.entity.Membresia;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClienteDelegate {

    private final ClienteDAO clienteDAO;

    public ClienteDelegate() {
        this.clienteDAO = ServiceLocator.getInstanceClienteDAO();
    }

    public void registrarCliente(Cliente cliente) throws Exception {
        //validar que el nombre no este vacio
        if (cliente.getNombreCompleto() == null || cliente.getNombreCompleto().trim().isEmpty()) {
            throw new Exception("el nombre no puede estar vacio.");
        }

        //validar que el telefono no este vacio
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            throw new Exception("el telefono no puede estar vacio.");
        }

        // trim inicial
        String rawTelefono = cliente.getTelefono().trim();

        // borra espacios y guiones
        String telefonoNormalizado = rawTelefono.replaceAll("[\\s\\-()]", "");

        //validar el formato del nombre
        if (!cliente.getNombreCompleto().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new Exception("el nombre solo puede contener letras y espacios.");
        }

        //validar el formato del telefono
        String normal = rawTelefono.replaceAll("[^0-9]", "");
        if (!normal.matches("\\d{7,15}")) {
            throw new Exception("Telefono invalido. Debe contener entre 7 y 15 dígitos.");
        }
        cliente.setTelefono(normal);

        String st = cliente.getSegundoTelefono();
        if (st != null && !st.trim().isEmpty()) {
            // limpiar formato
            String onlyDigits = st.replaceAll("[^0-9]", "");
            if (!onlyDigits.matches("\\d{7,15}")) {
                throw new Exception("Segundo teléfono inválido. Debe contener entre 7 y 15 dígitos.");
            }
            cliente.setSegundoTelefono(onlyDigits);
        } else {
            cliente.setSegundoTelefono(null);
        }

        cliente.setFechaRegistro(new Date());
        clienteDAO.crear(cliente);
    }

    /**
     * Metodo para hacer busqueda por ID en los clientes, llamara a la instancia de ClienteDAO
     * @Throws Si la base de datos rechaza la peticion de busqueda por ID
     * @Params Objeto de tipo String id
     * @return Una lista con los clientes que cumplen con id del cliente especificado lladado resultado
     */
    public List<Cliente> obtenerClientePorId(String id) {
        List<Cliente> resultado = new ArrayList<>();
        clienteDAO.find(id).ifPresent(resultado::add);
        return resultado;
    }

    public Cliente obtenerCliente(String id) {
        try {
            if (id == null) return null;
            id = id.trim();
            if (id.isEmpty()) return null;

            Cliente c = clienteDAO.find(id).orElse(null);
            return c;
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo cliente con id=" + id, e);
        }
    }

    /**
     * Metodo para hacer consulta de todos los clientes que llamara a la instancia de ClienteDAO
     * @Throws Si la base de datos rechaza la peticion de selec * from tabla
     * @return Una lista de clientes que contendra todos los clientes de la base de datos
     */
    public List<Cliente> listarClientes() {
        return clienteDAO.findAll();
    }

    //Metodo para eliminar el cliente
    public boolean eliminarCliente(String idCliente) throws Exception {
        if (idCliente == null || idCliente.trim().isEmpty()) {
            throw new Exception("El id del cliente esta vacio");
        }

        return clienteDAO.eliminarCliente(idCliente);
    }

    /*
    este metodo sirve para actualizar clientes mediante su ID
    que esta es proporcionada por el bean, falta implementar el bean
     */
    public void actualizarCliente(Cliente cliente) throws Exception {
        String idAActualizar = cliente.getIdCliente();
        if (idAActualizar == null || idAActualizar.trim().isEmpty()) {
            throw new Exception("No se proporcionó ID de cliente para actualizar.");
        }

        Cliente existente = clienteDAO.buscarPorId(idAActualizar);
        if (existente == null) {
            throw new Exception("No existe el cliente con ID " + idAActualizar + " en la base de datos.");
        }

        // Validaciones
        if (cliente.getNombreCompleto() == null || cliente.getNombreCompleto().trim().isEmpty()) {
            throw new Exception("El nombre no puede estar vacío.");
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            throw new Exception("El teléfono no puede estar vacío.");
        }

        String rawTelefono = cliente.getTelefono().trim();
        String normal = rawTelefono.replaceAll("[^0-9]", "");
        if (!normal.matches("\\d{7,15}")) {
            throw new Exception("Teléfono inválido. Debe contener entre 7 y 15 dígitos.");
        }
        if (!cliente.getNombreCompleto().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new Exception("El nombre solo puede contener letras y espacios.");
        }

        String sx = (cliente.getSexo() == null) ? "" : cliente.getSexo().trim().toLowerCase();
        if (!sx.equals("masculino") && !sx.equals("femenino")) {
            throw new Exception("Sexo inválido. Debe ser 'masculino' o 'femenino'.");
        }

        String st = cliente.getSegundoTelefono();
        String stNorm = null;
        if (st != null && !st.trim().isEmpty()) {
            String onlyDigits = st.replaceAll("[^0-9]", "");
            if (!onlyDigits.matches("\\d{7,15}")) {
                throw new Exception("Segundo teléfono inválido. Debe contener entre 7 y 15 dígitos.");
            }
            stNorm = onlyDigits;
        }

        // Aplicar cambios
        existente.setNombreCompleto(cliente.getNombreCompleto().trim());
        existente.setTelefono(normal);
        existente.setSexo(sx);
        existente.setSegundoTelefono(stNorm);

        clienteDAO.actualizar(existente);
    }

}