package restaurante.patterns.structural.bridge;

import restaurante.core.interfaces.INotificadorImplementador;

/**
 * Implementador Concreto: Email (SMTP)
 * Canal técnico para el envío de notificaciones por correo electrónico.
 * Responsable: Chris Grimaldo Cuya
 */
public class EmailImplementador implements INotificadorImplementador {

    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[BRIDGE - Email] Enviando correo a <" + destinatario + ">:");
        System.out.println("                 Asunto: Restaurante Gourmet | Mensaje: " + mensaje);
    }
}
