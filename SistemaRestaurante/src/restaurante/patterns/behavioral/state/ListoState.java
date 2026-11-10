package restaurante.patterns.behavioral.state;

import restaurante.core.interfaces.IEstadoPedido;
import restaurante.model.dto.Pedido;

/**
 * Patrón de Comportamiento: STATE (Estado Concreto: LISTO)
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public class ListoState implements IEstadoPedido {

    @Override
    public void siguienteEstado(Pedido pedido) {
        pedido.setEstadoActual(new EntregadoState());
        System.out.println("[STATE] Pedido entregado al cliente / repartidor. Ciclo completado.");
        pedido.notificarObservadores("LISTO", "ENTREGADO", "Pedido entregado con satisfacción.");
    }

    @Override
    public void cancelarPedido(Pedido pedido) {
        System.err.println("[STATE ERROR] No se puede cancelar un pedido que ya está listo.");
    }

    @Override
    public String getNombreEstado() {
        return "LISTO_PARA_ENTREGA";
    }
}
