package restaurante.patterns.creational.factorymethod;

import java.util.UUID;
import restaurante.model.dto.Pedido;

/**
 * Patrón Creacional: FACTORY METHOD (Creador Base)
 * Define el método fábrica para crear pedidos según el canal de venta.
 * Responsable: Zoe Mendoza Lalopú
 */
public abstract class PedidoFactory {

    public Pedido crearPedido(String cliente) {
        String idGenerado = "PED-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Pedido pedido = fabricarPedido(idGenerado, cliente);
        configurarReglasCanal(pedido);
        return pedido;
    }

    // Método Factory abstracto
    protected abstract Pedido fabricarPedido(String id, String cliente);

    // Gancho opcional para configurar reglas específicas del canal
    protected abstract void configurarReglasCanal(Pedido pedido);
}
