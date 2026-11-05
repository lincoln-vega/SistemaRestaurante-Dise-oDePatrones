package restaurante.patterns.creational.factorymethod;

import restaurante.model.dto.Pedido;

public class PedidoParaLlevarFactory extends PedidoFactory {

    @Override
    protected Pedido fabricarPedido(String id, String cliente) {
        Pedido pedido = new Pedido(id, cliente, "PARA_LLEVAR");

        pedido.setNotasCocina(
                "Pedido para recoger en el restaurante."
        );

        return pedido;
    }

    @Override
    protected void configurarReglasCanal(Pedido pedido) {
        // No requiere envio a domicilio.
    }
}