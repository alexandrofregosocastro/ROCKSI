package helper;

import mx.desarollo.entity.MovimientoCaja;
import mx.desarollo.integration.ServiceFacadeLocator;

import java.io.Serializable;

public class MovimientoHelper implements Serializable {

    /**
     * Metodo para consultar un movimiento de caja específico
     * @param idMovimiento El ID numérico del movimiento
     * @return El objeto MovimientoCaja encontrado o null si falla
     */
    public MovimientoCaja obtenerMovimiento(Integer idMovimiento) {
        try {
            return ServiceFacadeLocator.getInstanceMovimientoCajaFacade().obtenerMovimientoPorId(idMovimiento);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Metodo para registrar un nuevo movimiento de caja
     * @param movimiento El objeto MovimientoCaja ya lleno con datos
     * @throws Exception Si la base de datos rechaza la inserción
     */
    public void registrarMovimiento(MovimientoCaja movimiento) throws Exception {
        try {
            ServiceFacadeLocator.getInstanceMovimientoCajaFacade().registrarMovimiento(movimiento);
        } catch (Exception e) {
            throw new Exception("Error al registrar el movimiento: " + e.getMessage());
        }
    }

    /**
     * Metodo para conocer si ya se realizo una apartura en el dia
     * @throws Exception Si la base de datos rechaza la consulta
     */
    public boolean existeAperturaHoy() throws Exception {
        try {
            return ServiceFacadeLocator.getInstanceMovimientoCajaFacade().existeAperturaHoy();
        } catch (Exception e) {
            throw new Exception("Error al consultar apertura de hoy: " + e.getMessage());
        }
    }

    /**
     * Metodo para ontener la apertura del dia
     * @throws Exception Si la base de datos rechaza la consulta
     */
    public MovimientoCaja obtenerAperturaHoy() throws Exception {
        try {
            return ServiceFacadeLocator.getInstanceMovimientoCajaFacade().obtenerAperturaHoy();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Metodo para actualizar un mocimeinto de caja
     * @throws Exception Si la base de datos rechaza la actualizacion
     */
    public void actualizarMovimiento(MovimientoCaja movimiento) throws Exception {
        try {
            ServiceFacadeLocator.getInstanceMovimientoCajaFacade().actualizarMovimiento(movimiento);
        } catch (Exception e) {
            throw new Exception("Error en Helper al actualizar movimiento: " + e.getMessage());
        }
    }
}