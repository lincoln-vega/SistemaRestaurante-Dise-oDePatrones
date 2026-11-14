package restaurante.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de Persistencia Local (LocalStorage / Simulacion de BD sin servidor externo).
 * Permite almacenar, consultar, actualizar y vaciar pedidos en un archivo JSON local,
 * sirviendo como capa de persistencia desacoplada previa a la migracion a MySQL real.
 * Responsable: Lincoln Vega Hidalgo
 */
public class LocalStorageDB {
    private static final String ARCHIVO_BD = "localstorage_restaurante.json";
    private static LocalStorageDB instancia;

    public static synchronized LocalStorageDB getInstancia() {
        if (instancia == null) {
            instancia = new LocalStorageDB();
        }
        return instancia;
    }

    private LocalStorageDB() {
        inicializarArchivoSiNoExiste();
    }

    private void inicializarArchivoSiNoExiste() {
        File file = new File(ARCHIVO_BD);
        if (!file.exists()) {
            guardarDatosIniciales();
        }
    }

    private void guardarDatosIniciales() {
        String jsonInicial = "[\n" +
                "  {\n" +
                "    \"id\": \"PED-001\",\n" +
                "    \"cliente\": \"Lincoln Vega Hidalgo\",\n" +
                "    \"canal\": \"SALON\",\n" +
                "    \"items\": \"Hamburguesa Artesanal [Extra Queso Cheddar] + Papas\",\n" +
                "    \"total\": 29.50,\n" +
                "    \"pago\": \"Yape (910286670)\",\n" +
                "    \"estado\": \"LISTO\"\n" +
                "  }\n" +
                "]";
        escribirArchivo(jsonInicial);
    }

    public synchronized List<RegistroPedidoLocalStorage> listarPedidos() {
        List<RegistroPedidoLocalStorage> lista = new ArrayList<>();
        File file = new File(ARCHIVO_BD);
        if (!file.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea.trim());
            }
            String contenido = sb.toString();
            if (contenido.startsWith("[") && contenido.endsWith("]")) {
                contenido = contenido.substring(1, contenido.length() - 1).trim();
                if (!contenido.isEmpty()) {
                    String[] objetos = contenido.split("\\},\\s*\\{");
                    for (String obj : objetos) {
                        obj = obj.replace("{", "").replace("}", "");
                        RegistroPedidoLocalStorage reg = new RegistroPedidoLocalStorage();
                        String[] campos = obj.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                        for (String campo : campos) {
                            String[] kv = campo.split(":", 2);
                            if (kv.length == 2) {
                                String key = kv[0].trim().replace("\"", "");
                                String val = kv[1].trim().replace("\"", "");
                                switch (key) {
                                    case "id": reg.setId(val); break;
                                    case "cliente": reg.setCliente(val); break;
                                    case "canal": reg.setCanal(val); break;
                                    case "items": reg.setItems(val); break;
                                    case "total": reg.setTotal(Double.parseDouble(val)); break;
                                    case "pago": reg.setPago(val); break;
                                    case "estado": reg.setEstado(val); break;
                                }
                            }
                        }
                        if (reg.getId() != null) {
                            lista.add(reg);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[LOCALSTORAGE] Error leyendo pedidos: " + e.getMessage());
        }
        return lista;
    }

    public synchronized void guardarPedido(String id, String cliente, String canal, String items, double total, String pago, String estado) {
        List<RegistroPedidoLocalStorage> lista = listarPedidos();
        lista.removeIf(p -> p.getId().equalsIgnoreCase(id));
        lista.add(new RegistroPedidoLocalStorage(id, cliente, canal, items, total, pago, estado));
        guardarLista(lista);
    }

    public synchronized void actualizarEstado(String id, String nuevoEstado) {
        List<RegistroPedidoLocalStorage> lista = listarPedidos();
        for (RegistroPedidoLocalStorage p : lista) {
            if (p.getId().equalsIgnoreCase(id)) {
                p.setEstado(nuevoEstado);
                break;
            }
        }
        guardarLista(lista);
    }

    public synchronized void limpiarTodosLosPedidos() {
        escribirArchivo("[]");
        System.out.println("[LOCALSTORAGE] Historial de pedidos vaciado por el usuario.");
    }

    public synchronized void eliminarPedido(String id) {
        List<RegistroPedidoLocalStorage> lista = listarPedidos();
        lista.removeIf(p -> p.getId().equalsIgnoreCase(id));
        guardarLista(lista);
    }

    private void guardarLista(List<RegistroPedidoLocalStorage> lista) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < lista.size(); i++) {
            RegistroPedidoLocalStorage p = lista.get(i);
            sb.append("  {\n");
            sb.append("    \"id\": \"").append(escapar(p.getId())).append("\",\n");
            sb.append("    \"cliente\": \"").append(escapar(p.getCliente())).append("\",\n");
            sb.append("    \"canal\": \"").append(escapar(p.getCanal())).append("\",\n");
            sb.append("    \"items\": \"").append(escapar(p.getItems())).append("\",\n");
            sb.append("    \"total\": ").append(String.format(java.util.Locale.US, "%.2f", p.getTotal())).append(",\n");
            sb.append("    \"pago\": \"").append(escapar(p.getPago())).append("\",\n");
            sb.append("    \"estado\": \"").append(escapar(p.getEstado())).append("\"\n");
            sb.append("  }");
            if (i < lista.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        escribirArchivo(sb.toString());
    }

    private String escapar(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

    private void escribirArchivo(String contenido) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(ARCHIVO_BD), StandardCharsets.UTF_8)) {
            writer.write(contenido);
        } catch (IOException e) {
            System.err.println("[LOCALSTORAGE] Error escribiendo archivo: " + e.getMessage());
        }
    }

    public static class RegistroPedidoLocalStorage {
        private String id;
        private String cliente;
        private String canal;
        private String items;
        private double total;
        private String pago;
        private String estado;

        public RegistroPedidoLocalStorage() {}

        public RegistroPedidoLocalStorage(String id, String cliente, String canal, String items, double total, String pago, String estado) {
            this.id = id;
            this.cliente = cliente;
            this.canal = canal;
            this.items = items;
            this.total = total;
            this.pago = pago;
            this.estado = estado;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getCliente() { return cliente; }
        public void setCliente(String cliente) { this.cliente = cliente; }

        public String getCanal() { return canal; }
        public void setCanal(String canal) { this.canal = canal; }

        public String getItems() { return items; }
        public void setItems(String items) { this.items = items; }

        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }

        public String getPago() { return pago; }
        public void setPago(String pago) { this.pago = pago; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }
}
