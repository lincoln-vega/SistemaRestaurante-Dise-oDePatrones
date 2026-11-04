package restaurante.patterns.behavioral.memento;

import java.util.ArrayList;
import java.util.List;
import restaurante.core.interfaces.IComponenteMenu;

/**
 * Patrón de Comportamiento: MEMENTO
 * Guarda una instantánea del estado y los ítems del pedido para permitir "Deshacer/Revertir".
 * Responsable: Jairo Uriel Mejia Huerta
 */
public class PedidoMemento {
    private final String idPedido;
    private final List<IComponenteMenu> itemsSnapshot;
    private final String notasCocinaSnapshot;
    private final long timestamp;

    public PedidoMemento(String idPedido, List<IComponenteMenu> items, String notasCocina) {
        this.idPedido = idPedido;
        this.itemsSnapshot = new ArrayList<>(items);
        this.notasCocinaSnapshot = notasCocina;
        this.timestamp = System.currentTimeMillis();
    }

    public String getIdPedido() { return idPedido; }
    public List<IComponenteMenu> getItemsSnapshot() { return itemsSnapshot; }
    public String getNotasCocinaSnapshot() { return notasCocinaSnapshot; }
    public long getTimestamp() { return timestamp; }
}
