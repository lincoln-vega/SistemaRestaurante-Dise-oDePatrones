package restaurante.patterns.behavioral.state;

import restaurante.core.interfaces.IEstadoPedido;
import restaurante.model.dto.Pedido;

/**
 * Patrón de Comportamiento: STATE (Estado Concreto: REGISTRADO)
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public class RegistradoState implements IEstadoPedido {

    @Override
    public void siguienteEstado(Pedido pedido) {
        pedido.setEstadoActual(new EnCocinaState());
        pedido.notificarObservadores("REGISTRADO", "EN_COCINA", "El pedido pasó a preparación en cocina.");
    }

    @Override
    public void cancelarPedido(Pedido pedido) {
        System.out.println("[STATE] Pedido cancelado antes de ingresar a cocina.");
    }

    @Override
    public String getNombreEstado() {
        return "REGISTRADO";
    }
}
