
package restaurante.patterns.structural.bridge;

import restaurante.core.interfaces.INotificadorImplementador;

public class NotificacionComprobantePago extends Notificacion {

    public NotificacionComprobantePago(INotificadorImplementador implementador) {
        super(implementador);
    }

    @Override
    public void notificar(String destinatario, String mensaje) {
        implementador.enviar(destinatario, "[COMPROBANTE DE PAGO] " + mensaje);
    }
}
