package restaurante.core.facade;

import java.util.List;
import restaurante.config.ConfiguracionRestaurante;
import restaurante.config.ConexionDB;
import restaurante.core.interfaces.IComponenteMenu;
import restaurante.core.interfaces.IPago;
import restaurante.model.dao.PedidoDAO;
import restaurante.model.dto.Pedido;
import restaurante.model.dto.PlatoSimple;
import restaurante.patterns.behavioral.command.EnviarCocinaCommand;
import restaurante.patterns.behavioral.memento.HistorialPedidoCaretaker;
import restaurante.patterns.behavioral.memento.PedidoMemento;
import restaurante.patterns.behavioral.observer.CajaMonitorObserver;
import restaurante.patterns.behavioral.observer.CocinaMonitorObserver;
import restaurante.patterns.behavioral.state.RegistradoState;
import restaurante.patterns.creational.abstractfactory.IServiciosCanalFactory;
import restaurante.patterns.creational.abstractfactory.ServiciosDeliveryFactory;
import restaurante.patterns.creational.abstractfactory.ServiciosSalonFactory;
import restaurante.patterns.creational.builder.PedidoBuilder;
import restaurante.patterns.creational.factorymethod.PedidoDeliveryFactory;
import restaurante.patterns.creational.factorymethod.PedidoFactory;
import restaurante.patterns.creational.factorymethod.PedidoParaLlevarFactory;
import restaurante.patterns.creational.factorymethod.PedidoSalonFactory;
import restaurante.patterns.creational.prototype.CatalogoPlantillas;
import restaurante.patterns.structural.adapter.YapePaymentAdapter;
import restaurante.patterns.structural.composite.ComboMenuComposite;
import restaurante.patterns.structural.decorator.AdicionalExtraDecorator;
import restaurante.patterns.structural.proxy.AlmacenProxySeguro;

/**
 * Patrón Estructural: FACADE (Fachada Principal del Restaurante)
 * Centraliza y orquesta todas las operaciones del sistema, integrando los 15 patrones GOF
 * de las semanas 1 a 13 en un flujo de negocio desacoplado y elegante.
 * 
 * Responsable: Lincoln Vega Hidalgo (Líder Técnico)
 */
public class RestauranteFacade {

    private ConfiguracionRestaurante config;
    private AlmacenProxySeguro almacenProxy;
    private CatalogoPlantillas catalogoPlantillas;
    private PedidoDAO pedidoDAO;
    private HistorialPedidoCaretaker historialCaretaker;

    public RestauranteFacade() {
        // Inicializa Singletons
        this.config = ConfiguracionRestaurante.getInstancia();
        ConexionDB.getInstancia();

        // Inicializa subsistemas
        this.almacenProxy = new AlmacenProxySeguro("COCINA");
        this.catalogoPlantillas = new CatalogoPlantillas();
        this.pedidoDAO = new PedidoDAO();
        this.historialCaretaker = new HistorialPedidoCaretaker();

        // Registrar plantilla inicial de ejemplo (Patrón Prototype)
        precargarPlantillas();
    }

    private void precargarPlantillas() {
        Pedido plantillaEjecutiva = new Pedido("PROT-MENU-EJECUTIVO", "Cliente Frecuente", "SALON");
        plantillaEjecutiva.agregarItem(new PlatoSimple("P001", "Almuerzo Ejecutivo del Día", 15.00));
        catalogoPlantillas.registrarPlantilla("EJECUTIVO", plantillaEjecutiva);
    }

    /**
     * Muestra el membrete comercial oficial de la empresa utilizando el Singleton de Configuración.
     */
    public void mostrarMembreteEmpresa() {
        System.out.println("==========================================================================");
        System.out.println("  " + config.getNombreCadena().toUpperCase());
        System.out.println("  RUC: " + config.getRuc() + " | " + config.getDireccionMatriz());
        System.out.println("  Central Telefónica: " + config.getTelefonoCentral());
        System.out.println("==========================================================================");
    }

    /**
     * Procesa un pedido en canal SALÓN integrando Factory Method (Zoe), Abstract Factory (Zoe),
     * Proxy (Jairo), Decorator & Composite (Maria Isabel), Observer & State (Etxin), y Persistencia (Lincoln).
     */
    public Pedido procesarPedidoSalon(String cliente, int numeroMesa, List<IComponenteMenu> items, IPago metodoPago) {
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("🛎️  [CANAL SALÓN] Procesando atención para mesa #" + numeroMesa + " (" + cliente + ")");
        System.out.println("--------------------------------------------------------------------------");

        // 1. Zoe: Factory Method para Salón
        PedidoFactory salonFactory = new PedidoSalonFactory(numeroMesa);
        Pedido pedido = salonFactory.crearPedido(cliente);

        // 2. Zoe: Abstract Factory para Servicios de Salón
        IServiciosCanalFactory serviciosSalon = new ServiciosSalonFactory();
        if (metodoPago == null) {
            metodoPago = serviciosSalon.crearMetodoPagoPredeterminado();
        }
        pedido.setMetodoPago(metodoPago);

        // 3. Cargar Ítems
        for (IComponenteMenu item : items) {
            pedido.agregarItem(item);
        }

        // 4. Configurar Observadores y Estado Inicial
        configurarCicloDeVidaYObservadores(pedido);

        // 5. Memento (Jairo): Guardar instantánea antes de confirmación
        historialCaretaker.guardarEstado(new PedidoMemento(pedido.getIdPedido(), pedido.getItems(), pedido.getNotasCocina()));

        // 6. Cobro y Descuento
        double total = pedido.calcularTotal() + serviciosSalon.calcularCostoAdicional(pedido.calcularTotal());
        metodoPago.procesarPago(total, pedido.getIdPedido());

        // 7. Persistir vía DAO
        pedidoDAO.registrarPedido(pedido);

        return pedido;
    }

    /**
     * Procesa un pedido en canal DELIVERY con recargo adicional y validación de dirección.
     */
    public Pedido procesarPedidoDelivery(String cliente, String direccion, String telefono, List<IComponenteMenu> items, IPago metodoPago) {
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("🛵  [CANAL DELIVERY] Procesando despacho para: " + cliente + " -> " + direccion);
        System.out.println("--------------------------------------------------------------------------");

        // 1. Zoe: Factory Method para Delivery
        PedidoFactory deliveryFactory = new PedidoDeliveryFactory(direccion, telefono);
        Pedido pedido = deliveryFactory.crearPedido(cliente);

        // 2. Zoe: Abstract Factory para Servicios de Delivery (aplica recargo)
        IServiciosCanalFactory serviciosDelivery = new ServiciosDeliveryFactory();
        if (metodoPago == null) {
            metodoPago = serviciosDelivery.crearMetodoPagoPredeterminado();
        }
        pedido.setMetodoPago(metodoPago);

        for (IComponenteMenu item : items) {
            pedido.agregarItem(item);
        }

        configurarCicloDeVidaYObservadores(pedido);

        double recargo = serviciosDelivery.calcularCostoAdicional(pedido.calcularTotal());
        double totalFinal = pedido.calcularTotal() + recargo;
        System.out.println("   [RECARGO DELIVERY] Costo de envío aplicado: S/ " + String.format("%.2f", recargo));

        metodoPago.procesarPago(totalFinal, pedido.getIdPedido());
        pedidoDAO.registrarPedido(pedido);

        return pedido;
    }

    /**
     * Procesa un pedido en canal PARA LLEVAR (recojo en restaurante).
     */
    public Pedido procesarPedidoParaLlevar(String cliente, List<IComponenteMenu> items, IPago metodoPago) {
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("🥡  [CANAL PARA LLEVAR] Preparando recojo en mostrador para: " + cliente);
        System.out.println("--------------------------------------------------------------------------");

        PedidoFactory paraLlevarFactory = new PedidoParaLlevarFactory();
        Pedido pedido = paraLlevarFactory.crearPedido(cliente);

        if (metodoPago == null) {
            metodoPago = new YapePaymentAdapter("910286670");
        }
        pedido.setMetodoPago(metodoPago);

        for (IComponenteMenu item : items) {
            pedido.agregarItem(item);
        }

        configurarCicloDeVidaYObservadores(pedido);
        metodoPago.procesarPago(pedido.calcularTotal(), pedido.getIdPedido());
        pedidoDAO.registrarPedido(pedido);

        return pedido;
    }

    /**
     * Orquesta el avance de estado del pedido ejecutando un Command y notificando a la BD.
     */
    public void despacharACocina(Pedido pedido) {
        // Patrón Command (Etxin)
        EnviarCocinaCommand comando = new EnviarCocinaCommand(pedido);
        comando.ejecutar();
        pedidoDAO.actualizarEstado(pedido.getIdPedido(), pedido.getEstadoActual().getNombreEstado());
    }

    public void completarPedido(Pedido pedido) {
        pedido.avanzarEstado(); // Pasa a LISTO
        pedidoDAO.actualizarEstado(pedido.getIdPedido(), pedido.getEstadoActual().getNombreEstado());

        pedido.avanzarEstado(); // Pasa a ENTREGADO
        pedidoDAO.actualizarEstado(pedido.getIdPedido(), "ENTREGADO");
    }

    /**
     * Demostración rápida de flujo integral para pruebas de verificación.
     */
    public Pedido ejecutarFlujoIntegralDemostracion() {
        mostrarMembreteEmpresa();

        // 1. Verificar insumos con PROXY
        almacenProxy.verificarStock("CARNE_RES", 2);

        // 2. Armar platos con COMPOSITE y DECORATOR
        IComponenteMenu plato1 = new PlatoSimple("P001", "Hamburguesa Clásica", 18.50);
        IComponenteMenu platoConExtra = new AdicionalExtraDecorator(plato1, "Queso Cheddar Fundido", 3.00);

        IComponenteMenu bebida = new PlatoSimple("B001", "Gaseosa 500ml", 5.00);

        ComboMenuComposite comboFamiliar = new ComboMenuComposite("Combo Sabor Ejecutivo", 0.10);
        comboFamiliar.agregarComponente(platoConExtra);
        comboFamiliar.agregarComponente(bebida);

        // 3. Crear pedido en salón con Factory Method + Abstract Factory
        IPago pagoYape = new YapePaymentAdapter("910286670");
        Pedido pedido = procesarPedidoSalon("Lincoln Vega (Líder)", 7, java.util.Arrays.asList(comboFamiliar), pagoYape);

        // 4. Descontar stock con PROXY
        almacenProxy.descontarStock("CARNE_RES", 1);

        // 5. Despachar a cocina con COMMAND y transicionar con STATE + OBSERVER
        despacharACocina(pedido);
        completarPedido(pedido);

        System.out.println("\n🎉 [FACADE] Flujo completo ejecutado exitosamente con persistencia y 15 patrones sincronizados.");
        return pedido;
    }

    private void configurarCicloDeVidaYObservadores(Pedido pedido) {
        // Observer
        pedido.registrarObservador(new CocinaMonitorObserver());
        pedido.registrarObservador(new CajaMonitorObserver());

        // State inicial
        pedido.setEstadoActual(new RegistradoState());
    }

    // Getters
    public ConfiguracionRestaurante getConfig() { return config; }
    public CatalogoPlantillas getCatalogoPlantillas() { return catalogoPlantillas; }
    public AlmacenProxySeguro getAlmacenProxy() { return almacenProxy; }
    public PedidoDAO getPedidoDAO() { return pedidoDAO; }
}
