package restaurante.patterns.creational.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import restaurante.core.interfaces.IComponenteMenu;
import restaurante.core.interfaces.IPago;
import restaurante.model.dto.Pedido;

/**
 * Patrón Creacional: BUILDER
 * Permite construir pedidos complejos paso a paso con validaciones fluidas.
 * Responsable: Leonardo Luis Garay Llaja
 */
public class PedidoBuilder {
    private String idPedido;
    private String cliente;
    private String canalVenta = "SALON";
    private List<IComponenteMenu> items = new ArrayList<>();
    private IPago metodoPago;
    private String notasCocina = "";

    public PedidoBuilder conCliente(String cliente) {
        this.cliente = cliente;
        return this;
    }

    public PedidoBuilder enCanal(String canalVenta) {
        this.canalVenta = canalVenta;
        return this;
    }

    public PedidoBuilder agregarItem(IComponenteMenu item) {
        this.items.add(item);
        return this;
    }

    public PedidoBuilder conPago(IPago metodoPago) {
        this.metodoPago = metodoPago;
        return this;
    }

    public PedidoBuilder conNotas(String notas) {
        this.notasCocina = notas;
        return this;
    }

    public Pedido build() {
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalStateException("El pedido debe tener un cliente asignado.");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("El pedido debe tener al menos un ítem o plato.");
        }
        if (idPedido == null) {
            this.idPedido = "BLD-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        }

        Pedido pedido = new Pedido(this.idPedido, this.cliente, this.canalVenta);
        pedido.setItems(this.items);
        pedido.setMetodoPago(this.metodoPago);
        pedido.setNotasCocina(this.notasCocina);
        return pedido;
    }
}
