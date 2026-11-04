package restaurante.patterns.creational.abstractfactory;

import restaurante.core.interfaces.IPago;

/**
 * Patrón Creacional: ABSTRACT FACTORY
 * Fabrica familias de objetos relacionados o dependientes según el tipo de canal de venta.
 * Responsable: Zoe Mendoza Lalopú
 */
public interface IServiciosCanalFactory {
    IPago crearMetodoPagoPredeterminado();
    double calcularCostoAdicional(double subtotal);
    String getNombreCanal();
}
