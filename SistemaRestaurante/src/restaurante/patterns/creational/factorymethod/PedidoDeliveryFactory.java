package restaurante.patterns.creational.factorymethod;

import restaurante.model.dto.Pedido;

/**
 * Creador concreto para canal Delivery (envío a domicilio).
 * Responsable: Zoe Mendoza Lalopú
 */
public class PedidoDeliveryFactory extends PedidoFactory {
    private String direccionEntrega;
    private String telefono;

    public PedidoDeliveryFactory(String direccionEntrega, String telefono) {
        this.direccionEntrega = direccionEntrega;
        this.telefono = telefono;
    }

    @Override
    protected Pedido fabricarPedido(String id, String cliente) {
        Pedido pedido = new Pedido(id, cliente, "DELIVERY");
        pedido.setNotasCocina("Delivery para: " + direccionEntrega + " | Tel: " + telefono);
        return pedido;
    }

    @Override
    protected void configurarReglasCanal(Pedido pedido) {
        // En delivery se aplican validaciones de despacho
    }
}
