package restaurante.core.interfaces;

/**
 * Contrato de acceso al almacén e inventario de insumos.
 * Utilizado por el patrón Proxy (para control de permisos, caché y validación antes de la BD).
 * Responsable: Jairo Uriel Mejia Huerta
 */
public interface IAlmacenService {
    boolean verificarStock(String codigoInsumo, int cantidadRequerida);
    boolean descontarStock(String codigoInsumo, int cantidadADescontar);
}
