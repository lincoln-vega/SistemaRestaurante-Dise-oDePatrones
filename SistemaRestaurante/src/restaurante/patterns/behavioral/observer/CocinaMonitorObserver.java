package restaurante.patterns.behavioral.observer;

import restaurante.core.interfaces.IObservadorPedido;

/**
 * Patrón de Comportamiento: OBSERVER (Observador Concreto - Monitor de Cocina)
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public class CocinaMonitorObserver implements IObservadorPedido {
    @Override
    public void actualizar(String idPedido, String estadoAnterior, String nuevoEstado, String mensaje) {
        System.out.println("   [MONITOR COCINA] Pedido #" + idPedido + " cambió a " + nuevoEstado + " -> " + mensaje);
    }
}
