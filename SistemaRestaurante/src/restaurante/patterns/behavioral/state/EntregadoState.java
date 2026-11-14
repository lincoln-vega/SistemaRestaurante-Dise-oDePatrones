package restaurante.patterns.behavioral.state;

import restaurante.core.interfaces.IEstadoPedido;
import restaurante.model.dto.Pedido;

/**
 * Patrón de Comportamiento: STATE (Estado Concreto: ENTREGADO)
 * Representa el estado final donde el cliente o repartidor ya recibió el pedido.
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public class EntregadoState implements IEstadoPedido {

    @Override
    public void siguienteEstado(Pedido pedido) {
        System.out.println("[STATE] El pedido ya fue ENTREGADO. El ciclo de vida ha concluido.");
    }

    @Override
    public void cancelarPedido(Pedido pedido) {
        System.err.println("[STATE ERROR] No se puede cancelar un pedido que ya ha sido entregado.");
    }

    @Override
    public String getNombreEstado() {
        return "ENTREGADO";
    }
}
