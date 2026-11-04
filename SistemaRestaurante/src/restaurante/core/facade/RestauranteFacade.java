package restaurante.core.facade;

import restaurante.config.ConexionDB;
import restaurante.core.interfaces.IComponenteMenu;
import restaurante.core.interfaces.IPago;
import restaurante.model.dto.Pedido;
import restaurante.model.dto.PlatoSimple;
import restaurante.patterns.behavioral.observer.CajaMonitorObserver;
import restaurante.patterns.behavioral.observer.CocinaMonitorObserver;
import restaurante.patterns.behavioral.state.RegistradoState;
import restaurante.patterns.creational.builder.PedidoBuilder;
import restaurante.patterns.structural.adapter.YapePaymentAdapter;
import restaurante.patterns.structural.composite.ComboMenuComposite;
import restaurante.patterns.structural.decorator.AdicionalExtraDecorator;
import restaurante.patterns.structural.proxy.AlmacenProxySeguro;

/**
 * Patrón Estructural: FACADE (Fachada Principal del Restaurante)
 * Proporciona una interfaz unificada y simple para ejecutar todo el flujo del pedido.
 * Oculta la complejidad de los 15 subsistemas y patrones de diseño.
 * Responsable: Lincoln Vega Hidalgo
 */
public class RestauranteFacade {
    private AlmacenProxySeguro almacenProxy;

    public RestauranteFacade() {
        // Inicializa el Singleton de BD y el Proxy de Almacén
        ConexionDB.getInstancia();
        this.almacenProxy = new AlmacenProxySeguro("COCINA");
    }

    /**
     * Procesa un flujo completo de pedido demostrando la integración de patrones.
     */
    public Pedido procesarPedidoEjemplo(String cliente, String canal) {
        System.out.println("=================================================================");
        System.out.println("🍽️  [FACADE] INICIANDO NUEVO PEDIDO (" + canal + ") PARA: " + cliente);
        System.out.println("=================================================================");

        // 1. Verificar stock con PROXY
        almacenProxy.verificarStock("CARNE_RES", 1);

        // 2. Crear platos y aplicar COMPOSITE + DECORATOR
        IComponenteMenu hamburguesa = new PlatoSimple("P1", "Hamburguesa Clásica", 18.50);
        IComponenteMenu hamburguesaConQueso = new AdicionalExtraDecorator(hamburguesa, "Queso Cheddar", 3.00);

        IComponenteMenu gaseosa = new PlatoSimple("B1", "Gaseosa 500ml", 5.00);

        ComboMenuComposite comboFamiliar = new ComboMenuComposite("Combo Dúo Burger", 0.10);
        comboFamiliar.agregarComponente(hamburguesaConQueso);
        comboFamiliar.agregarComponente(gaseosa);

        System.out.println("\n--- Detalle del Menú Configurado ---");
        comboFamiliar.mostrarDetalle("");

        // 3. Crear pedido con BUILDER
        IPago metodoPago = new YapePaymentAdapter("910286670");
        Pedido pedido = new PedidoBuilder()
                .conCliente(cliente)
                .enCanal(canal)
                .agregarItem(comboFamiliar)
                .conPago(metodoPago)
                .conNotas("Bien cocido por favor")
                .build();

        // 4. Configurar OBSERVER
        pedido.registrarObservador(new CocinaMonitorObserver());
        pedido.registrarObservador(new CajaMonitorObserver());

        // 5. Configurar STATE inicial
        pedido.setEstadoActual(new RegistradoState());

        // 6. Procesar cobro con ADAPTER
        metodoPago.procesarPago(pedido.calcularTotal(), pedido.getIdPedido());

        // 7. Descontar stock con PROXY
        almacenProxy.descontarStock("CARNE_RES", 1);

        // 8. Transición de estados con STATE y notificaciones con OBSERVER
        System.out.println("\n--- Transición de Estados del Pedido ---");
        pedido.avanzarEstado(); // Pasa a EN_COCINA
        pedido.avanzarEstado(); // Pasa a LISTO
        pedido.avanzarEstado(); // Pasa a ENTREGADO

        System.out.println("\n✅ [FACADE] Pedido #" + pedido.getIdPedido() + " completado con éxito.");
        return pedido;
    }
}
