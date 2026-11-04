package restaurante.patterns.behavioral.command;

import restaurante.core.interfaces.IComando;
import restaurante.model.dto.Pedido;

/**
 * Patrón de Comportamiento: COMMAND (Comando Concreto)
 * Encapsula la orden de envío a cocina con capacidad de deshacer.
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public class EnviarCocinaCommand implements IComando {
    private Pedido pedido;

    public EnviarCocinaCommand(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void ejecutar() {
        System.out.println("[COMMAND] Ejecutando orden de cocina para pedido: " + pedido.getIdPedido());
        pedido.avanzarEstado();
    }

    @Override
    public void deshacer() {
        System.out.println("[COMMAND] Deshaciendo orden de cocina (reversión a estado previo).");
    }

    @Override
    public String getDescripcion() {
        return "Orden a Cocina para el pedido " + pedido.getIdPedido();
    }
}
