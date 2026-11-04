package restaurante.core.interfaces;

/**
 * Contrato para suscriptores de eventos del restaurante (Cocina, Caja, Almacén, Delivery).
 * Utilizado por el patrón Observer.
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public interface IObservadorPedido {
    void actualizar(String idPedido, String estadoAnterior, String nuevoEstado, String mensaje);
}
