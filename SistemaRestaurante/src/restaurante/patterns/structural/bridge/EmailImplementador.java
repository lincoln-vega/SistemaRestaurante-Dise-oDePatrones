
package restaurante.patterns.structural.bridge;

import restaurante.core.interfaces.INotificadorImplementador;

public class EmailImplementador implements INotificadorImplementador {

    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[BRIDGE - Email] Correo enviado a " + destinatario + ": " + mensaje);
    }
}