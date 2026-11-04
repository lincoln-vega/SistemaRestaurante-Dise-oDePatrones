package restaurante.patterns.structural.proxy;

import java.util.HashMap;
import java.util.Map;
import restaurante.core.interfaces.IAlmacenService;

/**
 * Patrón Estructural: PROXY
 * Intercepta y valida el acceso al almacén, verificando roles/permisos y manteniendo caché de stock.
 * Responsable: Jairo Uriel Mejia Huerta
 */
public class AlmacenProxySeguro implements IAlmacenService {
    private String rolUsuario;
    private Map<String, Integer> cacheStockMock = new HashMap<>();

    public AlmacenProxySeguro(String rolUsuario) {
        this.rolUsuario = rolUsuario;
        // Insumos iniciales de prueba
        cacheStockMock.put("CARNE_RES", 50);
        cacheStockMock.put("PAPAS_FRITAS", 100);
        cacheStockMock.put("GASEOSA_1L", 30);
    }

    @Override
    public boolean verificarStock(String codigoInsumo, int cantidadRequerida) {
        int disponible = cacheStockMock.getOrDefault(codigoInsumo, 0);
        boolean hayStock = disponible >= cantidadRequerida;
        System.out.println("[PROXY ALMACEN] Consulta de stock para '" + codigoInsumo + "': " 
                + disponible + " disponibles (Requerido: " + cantidadRequerida + ") -> " + (hayStock ? "OK" : "AGOTADO"));
        return hayStock;
    }

    @Override
    public boolean descontarStock(String codigoInsumo, int cantidadADescontar) {
        if (!"ADMIN".equalsIgnoreCase(rolUsuario) && !"COCINA".equalsIgnoreCase(rolUsuario)) {
            System.err.println("[PROXY SEGURIDAD DENEGADO] El rol '" + rolUsuario + "' no tiene permiso para descontar insumos.");
            return false;
        }

        if (verificarStock(codigoInsumo, cantidadADescontar)) {
            int actual = cacheStockMock.get(codigoInsumo);
            cacheStockMock.put(codigoInsumo, actual - cantidadADescontar);
            System.out.println("[PROXY ALMACEN] Descontado con éxito. Stock restante: " + (actual - cantidadADescontar));
            return true;
        }
        return false;
    }
}
