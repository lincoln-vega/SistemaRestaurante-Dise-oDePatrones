package restaurante.patterns.creational.abstractfactory;

import restaurante.core.interfaces.IPago;

public class ServiciosDeliveryFactory implements IServiciosCanalFactory {

    private static final double COSTO_ENVIO = 5.00;

    @Override
    public IPago crearMetodoPagoPredeterminado() {

        return new IPago() {

            @Override
            public boolean procesarPago(double monto, String referencia) {
                return monto > 0;
            }

            @Override
            public String getNombreMetodo() {
                return "Pago contra entrega";
            }
        };
    }

    @Override
    public double calcularCostoAdicional(double subtotal) {

        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal invalido");
        }

        return COSTO_ENVIO;
    }

    @Override
    public String getNombreCanal() {
        return "DELIVERY";
    }
}