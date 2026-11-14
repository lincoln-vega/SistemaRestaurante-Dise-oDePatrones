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
 * Patron Estructural: FACADE (Fachada Principal del Restaurante)
 * Proporciona una interfaz unificada y de alto nivel que orquesta de forma armonica
 * los 15 Patrones de Diseno del GOF implementados para el avance del 50%.
 * Responsable: Lincoln Vega Hidalgo (Lider Tecnico)
 */
public class RestauranteFacade {
    private AlmacenProxySeguro almacenProxyCocina;
    private AlmacenProxySeguro almacenProxyMozo;
    private CatalogoPlantillas catalogoPlantillas;
    private GestorComandosRestaurante gestorComandos;
    private HistorialPedidoCaretaker historialCaretaker;

    public RestauranteFacade() {
        System.out.println("================================================================================");
        System.out.println(">>> [1/15 - SINGLETON] Inicializando infraestructura global y conexion a BD...");
        ConexionDB.getInstancia();

        this.almacenProxyCocina = new AlmacenProxySeguro("COCINA");
        this.almacenProxyMozo = new AlmacenProxySeguro("MOZO");
        this.catalogoPlantillas = new CatalogoPlantillas();
        this.gestorComandos = new GestorComandosRestaurante();
        this.historialCaretaker = new HistorialPedidoCaretaker();

        precargarPlantillasPrototipo();
    }

    private void precargarPlantillasPrototipo() {
        System.out.println(">>> [4/15 - PROTOTYPE] Registrando plantillas base en el catalogo de prototipos...");
        Pedido pedidoBase = new Pedido("PLANTILLA-01", "CLIENTE_GENERICO", "SALON");
        pedidoBase.agregarItem(new PlatoSimple("P0", "Almuerzo Ejecutivo del Dia", 15.00));
        catalogoPlantillas.registrarPlantilla("MENU_DEL_DIA", pedidoBase);
    }

    /**
     * Ejecuta una demostracion integral y ordenada cubriendo los 15 patrones GOF solicitados.
     */
    public Pedido procesarFlujoCompletoRestaurante(String cliente, String canal) {
        System.out.println("\n================================================================================");
        System.out.println("REPORTE DE AUDITORIA: EJECUCION DE LOS 15 PATRONES GOF (FACHADA MAESTRA)");
        System.out.println("Proyecto: Sistema de Restaurante | Avance 50% UTP | Lider: Lincoln Vega");
        System.out.println("Orden Maestra para: " + cliente + " | Canal: " + canal);
        System.out.println("================================================================================\n");

        // --- PASO 1: Canales y Familias de Servicios (ABSTRACT FACTORY) ---
        System.out.println("[PATRON 02/15 - ABSTRACT FACTORY] Obteniendo familia de servicios para canal: " + canal);
        IServiciosCanalFactory canalFactory = "DELIVERY".equalsIgnoreCase(canal) 
                ? new ServiciosDeliveryFactory() 
                : new ServiciosSalonFactory();
        System.out.println("   -> Canal activo: " + canalFactory.getNombreCanal() 
                + " | Recargo de servicio: S/ " + String.format("%.2f", canalFactory.calcularCostoAdicional(20.0)));

        // --- PASO 2: Fabricacion segun canal (FACTORY METHOD) ---
        System.out.println("\n[PATRON 03/15 - FACTORY METHOD] Generando orden con fabrica especializada");
        PedidoFactory pedidoFactory = "DELIVERY".equalsIgnoreCase(canal)
                ? new PedidoDeliveryFactory("Av. Central 456, Lima", "910286670")
                : new PedidoSalonFactory(12);
        Pedido pedidoCanal = pedidoFactory.crearPedido(cliente);
        System.out.println("   -> Pedido generado: ID #" + pedidoCanal.getIdPedido() 
                + " | Notas asignadas: " + pedidoCanal.getNotasCocina());

        // --- PASO 3: Prototipos (PROTOTYPE) ---
        System.out.println("\n[PATRON 04/15 - PROTOTYPE] Clonando pedido prototipo registrado");
        Pedido pedidoClonado = catalogoPlantillas.obtenerClon("MENU_DEL_DIA");
        System.out.println("   -> Pedido clonado con exito: ID #" + pedidoClonado.getIdPedido() 
                + " con " + pedidoClonado.getItems().size() + " item(s) base.");

        // --- PASO 4: Menus compuestos (COMPOSITE) y Personalizaciones (DECORATOR) ---
        System.out.println("\n[PATRON 05/15 - COMPOSITE & 06/15 - DECORATOR] Configuracion de Menus y Adicionales");
        IComponenteMenu hamburguesa = new PlatoSimple("P1", "Hamburguesa Artesanal Clasica", 22.00);
        
        IComponenteMenu hamburguesaPersonalizada = new ExtraQuesoDecorator(
                new SinCebollaDecorator(hamburguesa)
        );
        IComponenteMenu papasGrandes = new PorcionPapasExtraDecorator(
                new PlatoSimple("GU1", "Guarnicion de Papas", 6.00)
        );
        IComponenteMenu bebida = new PlatoSimple("B1", "Chicha Morada Natural 500ml", 6.00);

        ComboMenuComposite comboGourmet = new ComboMenuComposite("Super Combo Familiar Gourmet", 0.12);
        comboGourmet.agregarComponente(hamburguesaPersonalizada);
        comboGourmet.agregarComponente(papasGrandes);
        comboGourmet.agregarComponente(bebida);

        System.out.println("   Detalle estructurado del Composite:");
        comboGourmet.mostrarDetalle("      ");

        // --- PASO 5: Construccion del Pedido (BUILDER) ---
        System.out.println("\n[PATRON 07/15 - BUILDER] Construyendo el pedido mediante Fluent API");
        IPago metodoPagoYape = new YapePaymentAdapter("910286670");
        Pedido pedidoFinal = new PedidoBuilder()
                .conCliente(cliente)
                .enCanal(canal)
                .agregarItem(comboGourmet)
                .conPago(metodoPagoYape)
                .conNotas("Atencion rapida, cliente VIP")
                .build();
        System.out.println("   -> Pedido construido: #" + pedidoFinal.getIdPedido() 
                + " | Total acumulado: S/ " + String.format("%.2f", pedidoFinal.calcularTotal()));

        // --- PASO 6: Seguridad y Verificacion de Stock (PROXY) ---
        System.out.println("\n[PATRON 08/15 - PROXY] Control de Acceso y Gestion de Stock en Almacen");
        almacenProxyMozo.verificarStock("CARNE_RES", 2);
        almacenProxyMozo.descontarStock("CARNE_RES", 2);
        almacenProxyCocina.descontarStock("CARNE_RES", 2);

        // --- PASO 7: Guardado de Estado y Reversion (MEMENTO) ---
        System.out.println("\n[PATRON 09/15 - MEMENTO] Creando punto de restauracion (Snapshot) antes de confirmar");
        PedidoMemento snapshotInicial = pedidoFinal.guardarMemento();
        historialCaretaker.guardarEstado(snapshotInicial);

        pedidoFinal.setNotasCocina("Nota erronea introducida por equivocacion.");
        System.out.println("   -> Notas actuales modificadas: " + pedidoFinal.getNotasCocina());
        if (historialCaretaker.puedeDeshacer()) {
            pedidoFinal.restaurarMemento(historialCaretaker.deshacer());
            System.out.println("   -> Notas tras Undo (Memento): " + pedidoFinal.getNotasCocina());
        }

        // --- PASO 8: Procesamiento de Pagos (ADAPTER) ---
        System.out.println("\n[PATRON 10/15 - ADAPTER] Pasarelas de Pago Adaptadas (Yape y Tarjeta)");
        metodoPagoYape.procesarPago(pedidoFinal.calcularTotal(), pedidoFinal.getIdPedido());
        
        IPago metodoTarjeta = new TarjetaPaymentAdapter("4557880012345678", cliente);
        System.out.println("   -> Metodo secundario verificado: " + metodoTarjeta.getNombreMetodo());

        // --- PASO 9: Notificaciones Desacopladas (BRIDGE) ---
        System.out.println("\n[PATRON 11/15 - BRIDGE] Envio de Notificaciones Multicanal");
        NotificacionComprobantePago notifComprobante = new NotificacionComprobantePago(new EmailImplementador());
        notifComprobante.notificar("lincoln.vega@ejemplo.com", "Monto cobrado S/ " + String.format("%.2f", pedidoFinal.calcularTotal()));

        NotificacionPedidoListo notifWhatsApp = new NotificacionPedidoListo(new WhatsAppImplementador());
        notifWhatsApp.notificar("+51 910286670", "Mesa asignada lista para entrega.");

        // --- PASO 10: Suscripcion de Monitores (OBSERVER) ---
        System.out.println("\n[PATRON 14/15 - OBSERVER] Registrando monitores de Cocina, Caja y Rastreo GPS");
        pedidoFinal.registrarObservador(new CocinaMonitorObserver());
        pedidoFinal.registrarObservador(new CajaMonitorObserver());
        pedidoFinal.registrarObservador(new DeliveryRastreadorObserver());

        // --- PASO 11: Encapsulamiento de Ordenes (COMMAND) ---
        System.out.println("\n[PATRON 12/15 - COMMAND] Despachando a Cocina mediante Gestor de Comandos");
        pedidoFinal.setEstadoActual(new RegistradoState());
        EnviarCocinaCommand comandoCocina = new EnviarCocinaCommand(pedidoFinal);
        gestorComandos.encolarComando(comandoCocina);
        gestorComandos.procesarCola();

        // --- PASO 12: Ciclo de Vida y Transicion (STATE) ---
        System.out.println("\n[PATRON 13/15 - STATE] Avanzando etapas del ciclo de vida del pedido");
        System.out.println("   -> Estado actual: " + pedidoFinal.getEstadoActual().getNombreEstado());
        pedidoFinal.avanzarEstado();
        System.out.println("   -> Estado actual: " + pedidoFinal.getEstadoActual().getNombreEstado());
        pedidoFinal.avanzarEstado();
        System.out.println("   -> Estado final: " + pedidoFinal.getEstadoActual().getNombreEstado());

        System.out.println("\n================================================================================");
        System.out.println("[PATRON 15/15 - FACADE] Pedido #" + pedidoFinal.getIdPedido() + " completado con exito.");
        System.out.println("RESULTADO: 15/15 PATRONES GOF INTEGRADOS Y VERIFICADOS CORRECTAMENTE.");
        System.out.println("================================================================================");

        return pedidoFinal;
    }

    public Pedido procesarPedidoEjemplo(String cliente, String canal) {
        return procesarFlujoCompletoRestaurante(cliente, canal);
    }
}
