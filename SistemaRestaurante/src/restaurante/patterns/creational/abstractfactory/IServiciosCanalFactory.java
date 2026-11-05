package restaurante.patterns.creational.abstractfactory;

import restaurante.core.interfaces.IPago;

public interface IServiciosCanalFactory {

    IPago crearMetodoPagoPredeterminado();

    double calcularCostoAdicional(double subtotal);

    String getNombreCanal();
}