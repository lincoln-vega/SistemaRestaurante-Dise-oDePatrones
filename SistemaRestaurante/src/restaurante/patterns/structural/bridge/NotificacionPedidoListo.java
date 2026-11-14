
package restaurante.patterns.structural.bridge;

import restaurante.core.interfaces.INotificadorImplementador;

public class NotificacionPedidoListo extends Notificacion {

    public NotificacionPedidoListo(INotificadorImplementador implementador) {
        super(implementador);
    }

    @Override
    public void notificar(String destinatario, String mensaje) {
        implementador.enviar(destinatario, "[PEDIDO LISTO] " + mensaje);
    }
}