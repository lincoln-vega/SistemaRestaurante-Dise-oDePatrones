<<<<<<< HEAD

=======
>>>>>>> origin/main
package restaurante.patterns.structural.bridge;

import restaurante.core.interfaces.INotificadorImplementador;

<<<<<<< HEAD
=======
/**
 * Abstraccion Refinada: Notificacion de Pedido Listo
 * Avisa al cliente cuando su comida esta preparada para entrega o recojo.
 * Responsable: Chris Grimaldo Cuya
 */
>>>>>>> origin/main
public class NotificacionPedidoListo extends Notificacion {

    public NotificacionPedidoListo(INotificadorImplementador implementador) {
        super(implementador);
    }

    @Override
    public void notificar(String destinatario, String mensaje) {
<<<<<<< HEAD
        implementador.enviar(destinatario, "[PEDIDO LISTO] " + mensaje);
    }
}
=======
        String formatoMensaje = "[AVISO DE COCINA] Tu pedido ya esta listo. " + mensaje;
        implementador.enviar(destinatario, formatoMensaje);
    }
}
>>>>>>> origin/main
