package restaurante.patterns.structural.adapter;

import restaurante.core.interfaces.IPago;

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
    }

    @Override
    public boolean procesarPago(double monto, String referencia) {
        System.out.println("[ADAPTER TARJETA] Conectando con Pasarela Visa/Mastercard...");
        System.out.println("[ADAPTER TARJETA] Titular: " + titular + " | Tarjeta: " + numeroTarjetaEnmascarado);
        System.out.println("[ADAPTER TARJETA] Cobro aprobado por: S/ " + String.format("%.2f", monto) + " (Ref: " + referencia + ")");
        return true;
    }

    @Override
    public String getNombreMetodo() {
        return "Tarjeta de Crédito/Débito (" + numeroTarjetaEnmascarado + ")";
    }
}
