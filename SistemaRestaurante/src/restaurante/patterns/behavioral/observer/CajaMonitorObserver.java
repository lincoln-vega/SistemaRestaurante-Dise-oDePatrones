package restaurante.patterns.behavioral.observer;

import restaurante.core.interfaces.IObservadorPedido;

/**
 * Patrón de Comportamiento: OBSERVER (Observador Concreto - Monitor de Caja)
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public class CajaMonitorObserver implements IObservadorPedido {
    @Override
    public void actualizar(String idPedido, String estadoAnterior, String nuevoEstado, String mensaje) {
        System.out.println("   [MONITOR CAJA] Pedido #" + idPedido + " registrado para facturación y cobro.");
    }
}
