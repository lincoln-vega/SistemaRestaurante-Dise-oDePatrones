<<<<<<< HEAD

=======
>>>>>>> origin/main
package restaurante.patterns.structural.bridge;

import restaurante.core.interfaces.INotificadorImplementador;

<<<<<<< HEAD
=======
/**
 * Implementador Concreto: Email (SMTP)
 * Canal técnico para el envío de notificaciones por correo electrónico.
 * Responsable: Chris Grimaldo Cuya
 */
>>>>>>> origin/main
public class EmailImplementador implements INotificadorImplementador {

    @Override
    public void enviar(String destinatario, String mensaje) {
<<<<<<< HEAD
        System.out.println("[BRIDGE - Email] Correo enviado a " + destinatario + ": " + mensaje);
    }
}
=======
        System.out.println("[BRIDGE - Email] Enviando correo a <" + destinatario + ">:");
        System.out.println("                 Asunto: Restaurante Gourmet | Mensaje: " + mensaje);
    }
}
>>>>>>> origin/main
