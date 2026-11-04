package restaurante.patterns.creational.factorymethod;

import restaurante.model.dto.Pedido;

/**
 * Creador concreto para canal Salón (atención en mesa).
 * Responsable: Zoe Mendoza Lalopú
 */
public class PedidoSalonFactory extends PedidoFactory {
    private int numeroMesa;

    public PedidoSalonFactory(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    @Override
    protected Pedido fabricarPedido(String id, String cliente) {
        Pedido pedido = new Pedido(id, cliente, "SALON");
        pedido.setNotasCocina("Mesa #" + numeroMesa + " (Atención mozo)");
        return pedido;
    }

    @Override
    protected void configurarReglasCanal(Pedido pedido) {
        // En salón no hay recargo por envío
    }
}
