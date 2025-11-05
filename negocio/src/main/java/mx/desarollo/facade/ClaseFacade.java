package mx.desarollo.facade;

import mx.desarollo.delegate.ClaseDelegate;
import mx.desarollo.entity.Clase;

import java.util.List;

public class ClaseFacade {
    private final ClaseDelegate claseDelegate = new ClaseDelegate();

    public void registrarClase(Clase clase) throws Exception {
        try {
            claseDelegate.registrarClase(clase);
        } catch (Exception e) {
            throw new Exception("Error al registrar la clase: " + e.getMessage());
        }
    }

    public boolean eliminarClase(String idClase) throws Exception {
        try{
            return claseDelegate.eliminarClase(idClase);
        } catch(Exception e){
            throw new Exception("Error al eliminar la clase: " + e.getMessage());
        }
    }

    /**
     * Metodo para hacer busqueda por ID en los clientes, llamara a la instancia de ClienteDelegate
     * @Throws Si la base de datos rechaza la peticion de busqueda por ID
     * @Params Objeto de tipo String id
     * @return Una lista con los clientes que cumplen con id del cliente especificado
     */
    public void actualizarClase(Clase cla) throws Exception {
        claseDelegate.actualizarClase(cla);
    }

    public Clase obtenerClasePorId(String id) {
        return claseDelegate.obtenerClase(id);
    }

    public List<Clase> listarClases() {
        return claseDelegate.listarClases();
    }
}
