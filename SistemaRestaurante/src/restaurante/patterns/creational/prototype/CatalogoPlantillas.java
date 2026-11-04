package restaurante.patterns.creational.prototype;

import java.util.HashMap;
import java.util.Map;
import restaurante.model.dto.Pedido;

/**
 * Patrón Creacional: PROTOTYPE (Registro de Prototipos)
 * Almacena pedidos estándar preconfigurados para clonarlos rápidamente sin rearmar todo.
 * Responsable: Leonardo Luis Garay Llaja
 */
public class CatalogoPlantillas {
    private Map<String, Pedido> plantillas = new HashMap<>();

    public void registrarPlantilla(String clave, Pedido prototipo) {
        plantillas.put(clave, prototipo);
    }

    public Pedido obtenerClon(String clave) {
        Pedido prototipo = plantillas.get(clave);
        if (prototipo != null) {
            return prototipo.clonar();
        }
        return null;
    }
}
