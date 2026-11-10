package restaurante.patterns.structural.bridge;

import restaurante.core.interfaces.INotificadorImplementador;

/**
 * Abstracción Refinada: Notificación de Comprobante de Pago
 * Envía el comprobante digital / boleta tras procesar el pago.
 * Responsable: Chris Grimaldo Cuya
 */
public class NotificacionComprobantePago extends Notificacion {

    public NotificacionComprobantePago(INotificadorImplementador implementador) {
        super(implementador);
    }

    @Override
    public void notificar(String destinatario, String mensaje) {
        String formatoMensaje = "🧾 [COMPROBANTE ELECTRÓNICO] Pago confirmado con éxito. " + mensaje;
        implementador.enviar(destinatario, formatoMensaje);
    }
}
