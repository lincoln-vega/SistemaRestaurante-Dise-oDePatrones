package restaurante.patterns.behavioral.command;

import restaurante.core.interfaces.IComando;
import restaurante.model.dto.Pedido;

/**
 * Patrón de Comportamiento: COMMAND (Comando Concreto: Cancelar Pedido)
 * Encapsula la orden de cancelación de un pedido con opción de reversión.
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public class CancelarPedidoCommand implements IComando {
    private Pedido pedido;

    public CancelarPedidoCommand(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void ejecutar() {
        System.out.println("[COMMAND] Ejecutando cancelación para el pedido: " + pedido.getIdPedido());
        pedido.cancelar();
    }

    @Override
    public void deshacer() {
        System.out.println("[COMMAND] Revertiendo cancelación: reactivando pedido " + pedido.getIdPedido());
    }

    @Override
    public String getDescripcion() {
        return "Cancelar pedido " + pedido.getIdPedido();
    }
}
