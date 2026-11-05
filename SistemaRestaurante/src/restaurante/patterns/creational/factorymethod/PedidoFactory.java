package restaurante.patterns.creational.factorymethod;

import java.util.UUID;
import restaurante.model.dto.Pedido;

public abstract class PedidoFactory {

    public Pedido crearPedido(String cliente) {
        String idGenerado = "PED-" +
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Pedido pedido = fabricarPedido(idGenerado, cliente);
        configurarReglasCanal(pedido);

        return pedido;
    }

    protected abstract Pedido fabricarPedido(String id, String cliente);

    protected abstract void configurarReglasCanal(Pedido pedido);
}