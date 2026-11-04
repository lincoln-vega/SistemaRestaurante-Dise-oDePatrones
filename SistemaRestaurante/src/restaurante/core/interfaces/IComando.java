package restaurante.core.interfaces;

/**
 * Contrato base para acciones encapsuladas en cola de ejecución (Cocina, Caja).
 * Utilizado por el patrón Command.
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public interface IComando {
    void ejecutar();
    void deshacer();
    String getDescripcion();
}
