package restaurante.model.dto;

import java.util.ArrayList;
import java.util.List;
import restaurante.core.interfaces.IComponenteMenu;
import restaurante.core.interfaces.IEstadoPedido;
import restaurante.core.interfaces.IObservadorPedido;
import restaurante.core.interfaces.IPago;
import restaurante.core.interfaces.IPrototipo;

/**
 * Entidad central del negocio.
 * Encapsula la información del pedido y sirve de contexto para State, Observer, Builder y Prototype.
 */
public class Pedido implements IPrototipo<Pedido> {
    private String idPedido;
    private String cliente;
    private String canalVenta; // SALON, DELIVERY, PARA_LLEVAR
    private List<IComponenteMenu> items;
    private IEstadoPedido estadoActual;
    private IPago metodoPago;
    private String notasCocina;
    private List<IObservadorPedido> observadores;

    public Pedido() {
        this.items = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    public Pedido(String idPedido, String cliente, String canalVenta) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.canalVenta = canalVenta;
        this.items = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    // Observer methods
    public void registrarObservador(IObservadorPedido obs) {
        if (!observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    public void removerObservador(IObservadorPedido obs) {
        observadores.remove(obs);
    }

    public void notificarObservadores(String anterior, String nuevo, String mensaje) {
        for (IObservadorPedido obs : observadores) {
            obs.actualizar(this.idPedido, anterior, nuevo, mensaje);
        }
    }

    // State transition
    public void avanzarEstado() {
        if (estadoActual != null) {
            estadoActual.siguienteEstado(this);
        }
    }

    public void cancelar() {
        if (estadoActual != null) {
            estadoActual.cancelarPedido(this);
        }
    }

    // Prototype implementation (deep-ish clone)
    @Override
    public Pedido clonar() {
        Pedido copia = new Pedido("CLON-" + System.currentTimeMillis() % 10000, this.cliente, this.canalVenta);
        copia.setNotasCocina(this.notasCocina);
        copia.items.addAll(this.items);
        return copia;
    }

    public double calcularTotal() {
        double subtotal = 0;
        for (IComponenteMenu item : items) {
            subtotal += item.getPrecio();
        }
        return subtotal;
    }

    public void agregarItem(IComponenteMenu item) {
        this.items.add(item);
    }

    // Getters y Setters
    public String getIdPedido() { return idPedido; }
    public void setIdPedido(String idPedido) { this.idPedido = idPedido; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getCanalVenta() { return canalVenta; }
    public void setCanalVenta(String canalVenta) { this.canalVenta = canalVenta; }

    public List<IComponenteMenu> getItems() { return items; }
    public void setItems(List<IComponenteMenu> items) { this.items = items; }

    public IEstadoPedido getEstadoActual() { return estadoActual; }
    public void setEstadoActual(IEstadoPedido estadoActual) { this.estadoActual = estadoActual; }

    public IPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(IPago metodoPago) { this.metodoPago = metodoPago; }

    public String getNotasCocina() { return notasCocina; }
    public void setNotasCocina(String notasCocina) { this.notasCocina = notasCocina; }
}
