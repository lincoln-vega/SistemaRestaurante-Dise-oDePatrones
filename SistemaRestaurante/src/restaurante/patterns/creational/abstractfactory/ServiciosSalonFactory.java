package restaurante.patterns.creational.abstractfactory;

import restaurante.core.interfaces.IPago;

public class ServiciosSalonFactory implements IServiciosCanalFactory {

    @Override
    public IPago crearMetodoPagoPredeterminado() {

        return new IPago() {

            @Override
            public boolean procesarPago(double monto, String referencia) {
                return monto > 0;
            }

            @Override
            public String getNombreMetodo() {
                return "Pago en caja";
            }
        };
    }

    @Override
    public double calcularCostoAdicional(double subtotal) {

        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal invalido");
        }

        return 0.0;
    }

    @Override
    public String getNombreCanal() {
        return "SALON";
    }
}