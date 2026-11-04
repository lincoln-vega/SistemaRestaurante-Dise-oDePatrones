package restaurante.core.interfaces;

/**
 * Contrato base para el patrón Prototype.
 * Permite clonar pedidos o configuraciones de menú frecuentemente solicitadas.
 * Responsable: Leonardo Luis Garay Llaja
 */
public interface IPrototipo<T> {
    T clonar();
}
