package mx.desarollo.facade;

import mx.desarollo.delegate.MovimientoCajaDelegate;
import mx.desarollo.entity.MovimientoCaja;

public class MovimientoCajaFacade {

    private final MovimientoCajaDelegate movimientoDelegate = new MovimientoCajaDelegate();

    public void registrarMovimiento(MovimientoCaja movimiento) throws Exception {
        try {
            movimientoDelegate.registrarMovimiento(movimiento);
        } catch (Exception e) {
            throw new Exception("Error al registrar el movimiento en Facade: " + e.getMessage());
        }
    }

    public MovimientoCaja obtenerMovimientoPorId(Integer idMovimiento) throws Exception {
        if (idMovimiento == null) {
            return null;
        }
        try {
            return movimientoDelegate.obtenerMovimientoPorId(idMovimiento);
        } catch (Exception e) {
            throw new Exception("Error al obtener el movimiento en Facade: " + e.getMessage());
        }
    }

    public boolean existeAperturaHoy() throws Exception {
        return movimientoDelegate.existeAperturaHoy();
    }

    public MovimientoCaja obtenerAperturaHoy() throws Exception {
        try {
            return movimientoDelegate.obtenerAperturaHoy();
        } catch (Exception e) {
            throw new Exception("Error al obtener la apertura de hoy en Facade: " + e.getMessage());
        }
    }

    public void actualizarMovimiento(MovimientoCaja movimiento) throws Exception {
        try {
            movimientoDelegate.actualizarMovimiento(movimiento);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el movimiento en Facade: " + e.getMessage());
        }
    }
}