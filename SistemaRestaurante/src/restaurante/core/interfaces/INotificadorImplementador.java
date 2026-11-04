package restaurante.core.interfaces;

/**
 * Contrato implementador para el patrón Bridge de notificaciones.
 * Permite desacoplar el tipo de notificación (alerta, factura) de la plataforma técnica (WhatsApp, SMS, Monitor).
 * Responsable: Chris Grimaldo Cuya
 */
public interface INotificadorImplementador {
    void enviar(String destinatario, String mensaje);
}
