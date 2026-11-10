package restaurante.patterns.structural.bridge;

import restaurante.core.interfaces.INotificadorImplementador;

/**
 * Abstraccion Refinada: Notificacion de Pedido Listo
 * Avisa al cliente cuando su comida esta preparada para entrega o recojo.
 * Responsable: Chris Grimaldo Cuya
 */
public class NotificacionPedidoListo extends Notificacion {

    public NotificacionPedidoListo(INotificadorImplementador implementador) {
        super(implementador);
    }

    @Override
    public void notificar(String destinatario, String mensaje) {
        String formatoMensaje = "[AVISO DE COCINA] Tu pedido ya esta listo. " + mensaje;
        implementador.enviar(destinatario, formatoMensaje);
    }
}
