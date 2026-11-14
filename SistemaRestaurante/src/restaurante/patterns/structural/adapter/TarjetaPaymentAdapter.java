
package restaurante.patterns.structural.adapter;

import restaurante.core.interfaces.IPago;

public class TarjetaPaymentAdapter implements IPago {

    private final PasarelaTarjetaExterna pasarelaExterna;
    private final String tarjetaEnmascarada;

    public TarjetaPaymentAdapter(String tarjetaEnmascarada) {
        this.pasarelaExterna = new PasarelaTarjetaExterna();
        this.tarjetaEnmascarada = tarjetaEnmascarada;
    }

    @Override
    public boolean procesarPago(double monto, String referencia) {
        String codigoOp = (referencia != null && !referencia.isBlank()) ? referencia : "TRX-SIN-REF";
        return pasarelaExterna.autorizarTransaccion(tarjetaEnmascarada, monto, codigoOp);
    }

    @Override
    public String getNombreMetodo() {
        return "Tarjeta Bancaria (" + tarjetaEnmascarada + ")";
    }
}
