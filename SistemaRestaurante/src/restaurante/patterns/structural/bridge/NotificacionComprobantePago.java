package restaurante.patterns.structural.bridge;

import restaurante.core.interfaces.INotificadorImplementador;

/**
 * Abstraccion Refinada: Notificacion de Comprobante de Pago
 * Envia el comprobante digital / boleta tras procesar el pago.
 * Responsable: Chris Grimaldo Cuya
 */
public class NotificacionComprobantePago extends Notificacion {

    public NotificacionComprobantePago(INotificadorImplementador implementador) {
        super(implementador);
    }

    @Override
    public void notificar(String destinatario, String mensaje) {
        String formatoMensaje = "[COMPROBANTE ELECTRONICO] Pago confirmado con exito. " + mensaje;
        implementador.enviar(destinatario, formatoMensaje);
    }
}
