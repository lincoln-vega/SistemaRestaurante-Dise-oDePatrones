package restaurante.core.interfaces;

import restaurante.model.dto.Pedido;

/**
 * Contrato de estado para el ciclo de vida del pedido.
 * Utilizado por el patrón State.
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public interface IEstadoPedido {
    void siguienteEstado(Pedido pedido);
    void cancelarPedido(Pedido pedido);
    String getNombreEstado();
}
