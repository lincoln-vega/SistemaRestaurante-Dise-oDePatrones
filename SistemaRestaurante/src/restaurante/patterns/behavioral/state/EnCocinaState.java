package restaurante.patterns.behavioral.state;

import restaurante.core.interfaces.IEstadoPedido;
import restaurante.model.dto.Pedido;

/**
 * Patrón de Comportamiento: STATE (Estado Concreto: EN COCINA)
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public class EnCocinaState implements IEstadoPedido {

    @Override
    public void siguienteEstado(Pedido pedido) {
        pedido.setEstadoActual(new ListoState());
        pedido.notificarObservadores("EN_COCINA", "LISTO", "Los platos están listos para servirse o despacharse.");
    }

    @Override
    public void cancelarPedido(Pedido pedido) {
        System.err.println("[STATE ALERTA] Cancelación con pedido ya en cocina requiere autorización.");
    }

    @Override
    public String getNombreEstado() {
        return "EN_COCINA";
    }
}
