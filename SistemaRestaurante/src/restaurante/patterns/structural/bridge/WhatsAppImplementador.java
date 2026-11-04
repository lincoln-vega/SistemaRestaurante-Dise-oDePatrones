package restaurante.patterns.structural.bridge;

import restaurante.core.interfaces.INotificadorImplementador;

/**
 * Implementador concreto para envío por WhatsApp.
 * Responsable: Chris Grimaldo Cuya
 */
public class WhatsAppImplementador implements INotificadorImplementador {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[BRIDGE - WhatsApp] Mensaje enviado al " + destinatario + ": " + mensaje);
    }
}
