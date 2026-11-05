package restaurante.patterns.creational.factorymethod;

import restaurante.model.dto.Pedido;

public class PedidoSalonFactory extends PedidoFactory {

    private int numeroMesa;

    public PedidoSalonFactory(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    @Override
    protected Pedido fabricarPedido(String id, String cliente) {
        Pedido pedido = new Pedido(id, cliente, "SALON");

        pedido.setNotasCocina(
                "Mesa #" + numeroMesa + " (Atencion mozo)"
        );

        return pedido;
    }

    @Override
    protected void configurarReglasCanal(Pedido pedido) {
        // No se aplica costo de envio.
    }
}