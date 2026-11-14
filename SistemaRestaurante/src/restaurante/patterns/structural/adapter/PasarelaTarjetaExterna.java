
package restaurante.patterns.structural.adapter;


public class PasarelaTarjetaExterna {
    public boolean autorizarTransaccion(String numeroTarjetaEnmascarado, double total, String codigoOperacion) {
        if (numeroTarjetaEnmascarado == null || numeroTarjetaEnmascarado.isBlank() || total <= 0) {
            return false;
        }
        System.out.println("[PASARELA TARJETA EXTERNA] Autorizando tarjeta "
                + numeroTarjetaEnmascarado + " por S/ " + String.format("%.2f", total)
                + " | Op: " + codigoOperacion);
        return true;
    }
}
