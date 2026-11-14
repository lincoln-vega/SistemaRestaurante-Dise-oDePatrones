<<<<<<< HEAD

=======
>>>>>>> origin/main
package restaurante.patterns.structural.adapter;

import restaurante.core.interfaces.IPago;

<<<<<<< HEAD
public class TarjetaPaymentAdapter implements IPago {

    private final PasarelaTarjetaExterna pasarelaExterna;
    private final String tarjetaEnmascarada;

    public TarjetaPaymentAdapter(String tarjetaEnmascarada) {
        this.pasarelaExterna = new PasarelaTarjetaExterna();
        this.tarjetaEnmascarada = tarjetaEnmascarada;
=======
/**
 * Patrón Estructural: ADAPTER (Tarjeta de Crédito / Débito)
 * Adapta la pasarela de pagos con tarjeta (Visa / Mastercard) a la interfaz IPago del sistema.
 * Responsable: Chris Grimaldo Cuya
 */
public class TarjetaPaymentAdapter implements IPago {
    private String numeroTarjetaEnmascarado;
    private String titular;

    public TarjetaPaymentAdapter(String numeroTarjeta, String titular) {
        this.titular = titular;
        if (numeroTarjeta != null && numeroTarjeta.length() >= 4) {
            this.numeroTarjetaEnmascarado = "**** **** **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4);
        } else {
            this.numeroTarjetaEnmascarado = "**** **** **** 0000";
        }
>>>>>>> origin/main
    }

    @Override
    public boolean procesarPago(double monto, String referencia) {
<<<<<<< HEAD
        String codigoOp = (referencia != null && !referencia.isBlank()) ? referencia : "TRX-SIN-REF";
        return pasarelaExterna.autorizarTransaccion(tarjetaEnmascarada, monto, codigoOp);
=======
        System.out.println("[ADAPTER TARJETA] Conectando con Pasarela Visa/Mastercard...");
        System.out.println("[ADAPTER TARJETA] Titular: " + titular + " | Tarjeta: " + numeroTarjetaEnmascarado);
        System.out.println("[ADAPTER TARJETA] Cobro aprobado por: S/ " + String.format("%.2f", monto) + " (Ref: " + referencia + ")");
        return true;
>>>>>>> origin/main
    }

    @Override
    public String getNombreMetodo() {
<<<<<<< HEAD
        return "Tarjeta Bancaria (" + tarjetaEnmascarada + ")";
=======
        return "Tarjeta de Crédito/Débito (" + numeroTarjetaEnmascarado + ")";
>>>>>>> origin/main
    }
}
