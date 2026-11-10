package restaurante.patterns.behavioral.observer;

import restaurante.core.interfaces.IObservadorPedido;

/**
 * Patron de Comportamiento: OBSERVER (Observador Concreto - Rastreador GPS / Delivery)
 * Notifica a la central de despachos y repartidores sobre el estado de la orden.
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public class DeliveryRastreadorObserver implements IObservadorPedido {

    @Override
    public void actualizar(String idPedido, String estadoAnterior, String nuevoEstado, String mensaje) {
        if ("LISTO".equalsIgnoreCase(nuevoEstado)) {
            System.out.println("   [MONITOR DELIVERY GPS] Pedido #" + idPedido + " Listo para despacho. Asignando repartidor disponible.");
        } else if ("ENTREGADO".equalsIgnoreCase(nuevoEstado)) {
            System.out.println("   [MONITOR DELIVERY GPS] Pedido #" + idPedido + " Marcado como Entregado en destino.");
        } else {
            System.out.println("   [MONITOR DELIVERY GPS] Pedido #" + idPedido + " en seguimiento (" + nuevoEstado + ").");
        }
    }
}
