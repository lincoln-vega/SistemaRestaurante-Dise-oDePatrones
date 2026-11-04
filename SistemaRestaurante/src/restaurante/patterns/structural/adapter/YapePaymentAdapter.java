package restaurante.patterns.structural.adapter;

import restaurante.core.interfaces.IPago;

/**
 * Patrón Estructural: ADAPTER
 * Adapta una API externa de billetera digital (Yape/Plin) a la interfaz IPago del sistema.
 * Responsable: Chris Grimaldo Cuya
 */
public class YapePaymentAdapter implements IPago {
    private String numeroCelular;

    public YapePaymentAdapter(String numeroCelular) {
        this.numeroCelular = numeroCelular;
    }

    @Override
    public boolean procesarPago(double monto, String referencia) {
        System.out.println("[ADAPTER YAPE] Conectando con API BCP Yape para el número " + numeroCelular);
        System.out.println("[ADAPTER YAPE] Cobro procesado con éxito: S/ " + String.format("%.2f", monto) + " (Ref: " + referencia + ")");
        return true;
    }

    @Override
    public String getNombreMetodo() {
        return "Billetera Digital YAPE (" + numeroCelular + ")";
    }
}
