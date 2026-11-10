package restaurante.core.facade;

import restaurante.config.ConexionDB;
import restaurante.core.interfaces.IComponenteMenu;
import restaurante.core.interfaces.IPago;
import restaurante.model.dto.Pedido;
import restaurante.model.dto.PlatoSimple;
import restaurante.patterns.behavioral.command.EnviarCocinaCommand;
import restaurante.patterns.behavioral.command.GestorComandosRestaurante;
import restaurante.patterns.behavioral.memento.HistorialPedidoCaretaker;
import restaurante.patterns.behavioral.memento.PedidoMemento;
import restaurante.patterns.behavioral.observer.CajaMonitorObserver;
import restaurante.patterns.behavioral.observer.CocinaMonitorObserver;
import restaurante.patterns.behavioral.observer.DeliveryRastreadorObserver;
import restaurante.patterns.behavioral.state.RegistradoState;
import restaurante.patterns.creational.abstractfactory.IServiciosCanalFactory;
import restaurante.patterns.creational.abstractfactory.ServiciosDeliveryFactory;
import restaurante.patterns.creational.abstractfactory.ServiciosSalonFactory;
import restaurante.patterns.creational.builder.PedidoBuilder;
import restaurante.patterns.creational.factorymethod.PedidoDeliveryFactory;
import restaurante.patterns.creational.factorymethod.PedidoFactory;
import restaurante.patterns.creational.factorymethod.PedidoSalonFactory;
import restaurante.patterns.creational.prototype.CatalogoPlantillas;
import restaurante.patterns.structural.adapter.TarjetaPaymentAdapter;
import restaurante.patterns.structural.adapter.YapePaymentAdapter;
import restaurante.patterns.structural.bridge.EmailImplementador;
import restaurante.patterns.structural.bridge.NotificacionComprobantePago;
import restaurante.patterns.structural.bridge.NotificacionPedidoListo;
import restaurante.patterns.structural.bridge.WhatsAppImplementador;
import restaurante.patterns.structural.composite.ComboMenuComposite;
import restaurante.patterns.structural.decorator.ExtraQuesoDecorator;
import restaurante.patterns.structural.decorator.PorcionPapasExtraDecorator;
import restaurante.patterns.structural.decorator.SinCebollaDecorator;
import restaurante.patterns.structural.proxy.AlmacenProxySeguro;

/**
 * Patrón Estructural: FACADE (Fachada Principal del Restaurante)
 * Proporciona una interfaz unificada y de alto nivel que orquesta de forma armónica
 * los 15 Patrones de Diseño del GOF implementados para el avance del 50%.
 * Responsable: Lincoln Vega Hidalgo (Líder Técnico)
 */
public class RestauranteFacade {
    private AlmacenProxySeguro almacenProxyCocina;
    private AlmacenProxySeguro almacenProxyMozo;
    private CatalogoPlantillas catalogoPlantillas;
    private GestorComandosRestaurante gestorComandos;
    private HistorialPedidoCaretaker historialCaretaker;

    public RestauranteFacade() {
        System.out.println(">>> [1/15 - SINGLETON] Inicializando infraestructura global y conexión a BD...");
        ConexionDB.getInstancia();

        // Inicializar servicios estructurales y de comportamiento
        this.almacenProxyCocina = new AlmacenProxySeguro("COCINA");
        this.almacenProxyMozo = new AlmacenProxySeguro("MOZO");
        this.catalogoPlantillas = new CatalogoPlantillas();
        this.gestorComandos = new GestorComandosRestaurante();
        this.historialCaretaker = new HistorialPedidoCaretaker();

        precargarPlantillasPrototipo();
    }

    /**
     * Inicializa plantillas reutilizables mediante el patrón PROTOTYPE.
     */
    private void precargarPlantillasPrototipo() {
        System.out.println(">>> [4/15 - PROTOTYPE] Registrando plantillas base en el catálogo de prototipos...");
        Pedido pedidoBase = new Pedido("PLANTILLA-01", "CLIENTE_GENERICO", "SALON");
        pedidoBase.agregarItem(new PlatoSimple("P0", "Almuerzo Ejecutivo del Día", 15.00));
        catalogoPlantillas.registrarPlantilla("MENU_DEL_DIA", pedidoBase);
    }

    /**
     * Ejecuta una demostración integral paso a paso cubriendo los 15 patrones GOF solicitados en la Guía Oficial.
     */
    public Pedido procesarFlujoCompletoRestaurante(String cliente, String canal) {
        System.out.println("\n================================================================================");
        System.out.println("🍽️  [15/15 - FACADE] PROCESANDO ORDEN MAESTRA (" + canal + ") PARA: " + cliente);
        System.out.println("================================================================================");

        // --- PASO 1: Canales y Familias de Servicios (ABSTRACT FACTORY) ---
        System.out.println("\n--- [2/15 - ABSTRACT FACTORY] Obteniendo familia de servicios para canal: " + canal + " ---");
        IServiciosCanalFactory canalFactory = "DELIVERY".equalsIgnoreCase(canal) 
                ? new ServiciosDeliveryFactory() 
                : new ServiciosSalonFactory();
        System.out.println("Canal activo: " + canalFactory.getNombreCanal() 
                + " | Recargo de servicio: S/ " + String.format("%.2f", canalFactory.calcularCostoAdicional(20.0)));

        // --- PASO 2: Fabricación según canal (FACTORY METHOD) ---
        System.out.println("\n--- [3/15 - FACTORY METHOD] Generando orden con fábrica especializada ---");
        PedidoFactory pedidoFactory = "DELIVERY".equalsIgnoreCase(canal)
                ? new PedidoDeliveryFactory("Av. Central 456, Lima", "910286670")
                : new PedidoSalonFactory(12);
        Pedido pedidoCanal = pedidoFactory.crearPedido(cliente);
        System.out.println("Pedido generado vía Factory Method: ID #" + pedidoCanal.getIdPedido() 
                + " | Notas asignadas: " + pedidoCanal.getNotasCocina());

        // --- PASO 3: Prototipos (PROTOTYPE) ---
        System.out.println("\n--- [4/15 - PROTOTYPE] Clonando pedido prototipo registrado ---");
        Pedido pedidoClonado = catalogoPlantillas.obtenerClon("MENU_DEL_DIA");
        System.out.println("Pedido prototipo clonado exitosamente: ID #" + pedidoClonado.getIdPedido() 
                + " con " + pedidoClonado.getItems().size() + " ítem(s) base.");

        // --- PASO 4: Menús compuestos (COMPOSITE) y Personalizaciones (DECORATOR) ---
        System.out.println("\n--- [5/15 - COMPOSITE & 6/15 - DECORATOR] Configuración de Menús y Extras ---");
        IComponenteMenu hamburguesa = new PlatoSimple("P1", "Hamburguesa Artesanal Clásica", 22.00);
        
        // Aplicando decoradores concretos: ExtraQueso, SinCebolla y PorcionPapasExtra
        IComponenteMenu hamburguesaPersonalizada = new ExtraQuesoDecorator(
                new SinCebollaDecorator(hamburguesa)
        );
        IComponenteMenu papasGrandes = new PorcionPapasExtraDecorator(
                new PlatoSimple("GU1", "Guarnición de Papas", 6.00)
        );
        IComponenteMenu bebida = new PlatoSimple("B1", "Chicha Morada Natural 500ml", 6.00);

        ComboMenuComposite comboGourmet = new ComboMenuComposite("Super Combo Familiar Gourmet", 0.12);
        comboGourmet.agregarComponente(hamburguesaPersonalizada);
        comboGourmet.agregarComponente(papasGrandes);
        comboGourmet.agregarComponente(bebida);

        System.out.println("\nDetalle estructurado del Composite:");
        comboGourmet.mostrarDetalle("  ");

        // --- PASO 5: Construcción del Pedido (BUILDER) ---
        System.out.println("\n--- [7/15 - BUILDER] Construyendo el pedido mediante Fluent API ---");
        IPago metodoPagoYape = new YapePaymentAdapter("910286670");
        Pedido pedidoFinal = new PedidoBuilder()
                .conCliente(cliente)
                .enCanal(canal)
                .agregarItem(comboGourmet)
                .conPago(metodoPagoYape)
                .conNotas("Atención rápida, cliente VIP")
                .build();
        System.out.println("Pedido construido exitosamente: #" + pedidoFinal.getIdPedido() 
                + " | Total acumulado: S/ " + String.format("%.2f", pedidoFinal.calcularTotal()));

        // --- PASO 6: Seguridad y Verificación de Stock (PROXY) ---
        System.out.println("\n--- [8/15 - PROXY] Control de Acceso y Gestión de Stock en Almacén ---");
        // Mozo verifica stock (Permitido)
        almacenProxyMozo.verificarStock("CARNE_RES", 2);
        // Mozo intenta descontar stock (Bloqueado por seguridad)
        almacenProxyMozo.descontarStock("CARNE_RES", 2);
        // Cocina descuenta stock (Permitido)
        almacenProxyCocina.descontarStock("CARNE_RES", 2);

        // --- PASO 7: Guardado de Estado y Reversión (MEMENTO) ---
        System.out.println("\n--- [9/15 - MEMENTO] Creando punto de restauración (Snapshot) antes de confirmar ---");
        PedidoMemento snapshotInicial = pedidoFinal.guardarMemento();
        historialCaretaker.guardarEstado(snapshotInicial);

        // Simulamos un cambio indebido por error del operador
        pedidoFinal.setNotasCocina("Nota errónea introducida por equivocación.");
        System.out.println("Notas actuales modificadas: " + pedidoFinal.getNotasCocina());
        // Deshacemos con el Caretaker
        if (historialCaretaker.puedeDeshacer()) {
            pedidoFinal.restaurarMemento(historialCaretaker.deshacer());
            System.out.println("Notas tras Undo (Memento): " + pedidoFinal.getNotasCocina());
        }

        // --- PASO 8: Procesamiento de Pagos (ADAPTER) ---
        System.out.println("\n--- [10/15 - ADAPTER] Pasarelas de Pago Adaptadas (Yape y Tarjeta) ---");
        metodoPagoYape.procesarPago(pedidoFinal.calcularTotal(), pedidoFinal.getIdPedido());
        
        // Demostramos también el segundo Adapter (Tarjeta de Crédito)
        IPago metodoTarjeta = new TarjetaPaymentAdapter("4557880012345678", cliente);
        System.out.println("Método secundario configurado: " + metodoTarjeta.getNombreMetodo());

        // --- PASO 9: Notificaciones Desacopladas (BRIDGE) ---
        System.out.println("\n--- [11/15 - BRIDGE] Envío de Notificaciones Multicanal ---");
        NotificacionComprobantePago notifComprobante = new NotificacionComprobantePago(new EmailImplementador());
        notifComprobante.notificar("lincoln.vega@ejemplo.com", "Monto cobrado S/ " + String.format("%.2f", pedidoFinal.calcularTotal()));

        NotificacionPedidoListo notifWhatsApp = new NotificacionPedidoListo(new WhatsAppImplementador());
        notifWhatsApp.notificar("+51 910286670", "Mesa asignada lista para entrega.");

        // --- PASO 10: Suscripción de Monitores (OBSERVER) ---
        System.out.println("\n--- [14/15 - OBSERVER] Registrando monitores de Cocina, Caja y Rastreo GPS ---");
        pedidoFinal.registrarObservador(new CocinaMonitorObserver());
        pedidoFinal.registrarObservador(new CajaMonitorObserver());
        pedidoFinal.registrarObservador(new DeliveryRastreadorObserver());

        // --- PASO 11: Encapsulamiento de Órdenes (COMMAND) ---
        System.out.println("\n--- [12/15 - COMMAND] Despachando a Cocina mediante Gestor de Comandos ---");
        pedidoFinal.setEstadoActual(new RegistradoState());
        EnviarCocinaCommand comandoCocina = new EnviarCocinaCommand(pedidoFinal);
        gestorComandos.encolarComando(comandoCocina);
        gestorComandos.procesarCola();

        // --- PASO 12: Ciclo de Vida y Transición (STATE) ---
        System.out.println("\n--- [13/15 - STATE] Avanzando etapas del ciclo de vida del pedido ---");
        System.out.println("Estado actual: " + pedidoFinal.getEstadoActual().getNombreEstado());
        pedidoFinal.avanzarEstado(); // Pasa de EN_COCINA -> LISTO (notifica observadores)
        System.out.println("Estado actual: " + pedidoFinal.getEstadoActual().getNombreEstado());
        pedidoFinal.avanzarEstado(); // Pasa de LISTO -> ENTREGADO (notifica observadores)
        System.out.println("Estado final: " + pedidoFinal.getEstadoActual().getNombreEstado());

        System.out.println("\n================================================================================");
        System.out.println("✅ [FACADE] Pedido #" + pedidoFinal.getIdPedido() + " completado. 15/15 Patrones GOF verificados.");
        System.out.println("================================================================================");

        return pedidoFinal;
    }

    /**
     * Sobrecarga de compatibilidad para procesar pedidos de ejemplo.
     */
    public Pedido procesarPedidoEjemplo(String cliente, String canal) {
        return procesarFlujoCompletoRestaurante(cliente, canal);
    }
}
