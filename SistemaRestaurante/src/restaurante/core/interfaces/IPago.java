package restaurante.core.interfaces;

/**
 * Contrato base para el procesamiento de pagos en el restaurante.
 * Utilizado por el patrón Adapter (para adaptar Yape, Plin, Tarjeta, etc.).
 * Responsable: Chris Grimaldo Cuya
 */
public interface IPago {
    boolean procesarPago(double monto, String referencia);
    String getNombreMetodo();
}
