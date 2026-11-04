package restaurante.patterns.structural.bridge;

import restaurante.core.interfaces.INotificadorImplementador;

/**
 * Patrón Estructural: BRIDGE (Abstracción)
 * Desacopla la lógica de negocio de las notificaciones de los canales técnicos de envío.
 * Responsable: Chris Grimaldo Cuya
 */
public abstract class Notificacion {
    protected INotificadorImplementador implementador;

    public Notificacion(INotificadorImplementador implementador) {
        this.implementador = implementador;
    }

    public abstract void notificar(String destinatario, String mensaje);
}
