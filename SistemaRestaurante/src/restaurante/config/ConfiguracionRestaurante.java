package restaurante.config;

/**
 * Patrón Creacional: SINGLETON
 * Administra la configuración global del restaurante (Nombre, RUC, IGV, Moneda).
 * Garantiza que toda la aplicación consulte una única fuente de verdad comercial.
 * Responsable: Lincoln Vega Hidalgo
 */
public class ConfiguracionRestaurante {
    private static volatile ConfiguracionRestaurante instancia;

    private String nombreCadena;
    private String ruc;
    private double tasaIGV; // 0.18 (18%)
    private String moneda;
    private String direccionMatriz;
    private String telefonoCentral;

    private ConfiguracionRestaurante() {
        // Configuración predeterminada de la cadena
        this.nombreCadena = "Cadena de Restaurantes Sabor & Tradición S.A.C.";
        this.ruc = "20601234567";
        this.tasaIGV = 0.18;
        this.moneda = "S/ ";
        this.direccionMatriz = "Av. Javier Prado Este 2465, Lima";
        this.telefonoCentral = "(01) 619-8000";
    }

    public static ConfiguracionRestaurante getInstancia() {
        if (instancia == null) {
            synchronized (ConfiguracionRestaurante.class) {
                if (instancia == null) {
                    instancia = new ConfiguracionRestaurante();
                }
            }
        }
        return instancia;
    }

    public double calcularIGV(double subtotal) {
        return subtotal * tasaIGV;
    }

    public double calcularTotalConIGV(double subtotal) {
        return subtotal * (1.0 + tasaIGV);
    }

    // Getters y Setters
    public String getNombreCadena() { return nombreCadena; }
    public void setNombreCadena(String nombreCadena) { this.nombreCadena = nombreCadena; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public double getTasaIGV() { return tasaIGV; }
    public void setTasaIGV(double tasaIGV) { this.tasaIGV = tasaIGV; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public String getDireccionMatriz() { return direccionMatriz; }
    public void setDireccionMatriz(String direccionMatriz) { this.direccionMatriz = direccionMatriz; }

    public String getTelefonoCentral() { return telefonoCentral; }
    public void setTelefonoCentral(String telefonoCentral) { this.telefonoCentral = telefonoCentral; }
}
