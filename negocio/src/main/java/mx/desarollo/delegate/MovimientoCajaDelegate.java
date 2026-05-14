package mx.desarollo.delegate;

import mx.avanti.desarollo.dao.MovimientoCajaDAO;
import mx.avanti.desarollo.integration.ServiceLocator;
import mx.desarollo.entity.MovimientoCaja;

public class MovimientoCajaDelegate {

    private final MovimientoCajaDAO movimientoDAO;

    public MovimientoCajaDelegate() {
        this.movimientoDAO = ServiceLocator.getInstanceMovimientoCajaDAO();
    }

    public void registrarMovimiento(MovimientoCaja movimiento) throws Exception {
        // Validaciones basicas de seguridad
        if (movimiento.getMonto() == null || movimiento.getMonto() <= 0) {
            throw new Exception("El monto del movimiento debe ser válido y mayor a cero.");
        }
        if (movimiento.getIdUsuario() == null || movimiento.getIdUsuario().trim().isEmpty()) {
            throw new Exception("Falta identificar al usuario que realiza el movimiento.");
        }

        movimientoDAO.registrarMovimiento(movimiento);
    }

    public MovimientoCaja obtenerMovimientoPorId(Integer idMovimiento) throws Exception {
        return movimientoDAO.obtenerMovimientoPorId(idMovimiento);
    }

    public boolean existeAperturaHoy() throws Exception {
        return movimientoDAO.existeAperturaHoy();
    }

    public MovimientoCaja obtenerAperturaHoy() throws Exception {
        return movimientoDAO.obtenerAperturaHoy();
    }

    public void actualizarMovimiento(MovimientoCaja movimiento) throws Exception {
        if (movimiento == null || movimiento.getIdMovimiento() == null) {
            throw new Exception("No se puede actualizar un movimiento inexistente.");
        }
        if (movimiento.getMonto() == null || movimiento.getMonto() <= 0) {
            throw new Exception("El monto corregido debe ser mayor a cero.");
        }
        movimientoDAO.actualizarMovimiento(movimiento);
    }
}
