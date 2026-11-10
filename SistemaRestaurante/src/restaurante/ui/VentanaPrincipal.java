package restaurante.ui;

import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.UUID;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import restaurante.config.ConexionDB;
import restaurante.core.facade.RestauranteFacade;
import restaurante.core.interfaces.IComponenteMenu;
import restaurante.core.interfaces.IObservadorPedido;
import restaurante.core.interfaces.IPago;
import restaurante.model.dto.Pedido;
import restaurante.model.dto.PlatoSimple;
import restaurante.patterns.behavioral.command.CancelarPedidoCommand;
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
 * Interfaz Gráfica de Usuario (Java Swing)
 * Demostrador visual interactivo de los 15 Patrones de Diseño GOF.
 * Proyecto: Sistema de Gestión de Pedidos - Restaurante (Avance 50% - UTP)
 * Responsable Técnico: Lincoln Vega Hidalgo
 */
public class VentanaPrincipal extends JFrame implements IObservadorPedido {

    // Componentes de interfaz
    private JTextField txtCliente;
    private JComboBox<String> cmbCanal;
    private JTextField txtDetalleCanal;
    private JComboBox<String> cmbPlato;
    private JCheckBox chkExtraQueso;
    private JCheckBox chkPapasExtra;
    private JCheckBox chkSinCebolla;
    private JCheckBox chkCombo;
    private JRadioButton rbtnYape;
    private JRadioButton rbtnTarjeta;
    private JComboBox<String> cmbRolProxy;
    private JTextArea txtConsola;

    // Indicadores visuales de Estado
    private JLabel lblEstadoRegistrado;
    private JLabel lblEstadoCocina;
    private JLabel lblEstadoListo;
    private JLabel lblEstadoEntregado;

    // Estado del pedido interactivo
    private Pedido pedidoActual;
    private RestauranteFacade facade;
    private CatalogoPlantillas catalogoPlantillas;
    private HistorialPedidoCaretaker caretaker;
    private GestorComandosRestaurante gestorComandos;
    private AlmacenProxySeguro proxyAlmacen;

    public VentanaPrincipal() {
        super("Sistema de Restaurante - 15 Patrones de Diseño GOF (Avance 50% UTP)");
        inicializarArquitectura();
        inicializarComponentes();
        redirigirConsolaASwing();
    }

    private void inicializarArquitectura() {
        this.facade = new RestauranteFacade();
        this.catalogoPlantillas = new CatalogoPlantillas();
        this.caretaker = new HistorialPedidoCaretaker();
        this.gestorComandos = new GestorComandosRestaurante();
        this.proxyAlmacen = new AlmacenProxySeguro("COCINA");

        // Registrar plantilla para PROTOTYPE
        Pedido plantilla = new Pedido("PLANTILLA-EXEC", "Cliente Frecuente", "SALON");
        plantilla.agregarItem(new PlatoSimple("P-EJEC", "Menú Ejecutivo Completo", 16.50));
        catalogoPlantillas.registrarPlantilla("EJECUTIVO", plantilla);
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 820);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // --- ENCABEZADO SUPERIOR ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(26, 54, 93)); // Navy elegante
        panelHeader.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitulo = new JLabel("🍽️ SISTEMA DE GESTIÓN DE PEDIDOS - CADENA DE RESTAURANTES");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("UTP | Curso: Diseño de Patrones (Semana 14 - Avance 50%) | Líder Técnico: Lincoln Vega Hidalgo");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(226, 232, 240));

        panelHeader.add(lblTitulo, BorderLayout.NORTH);
        panelHeader.add(lblSubtitulo, BorderLayout.SOUTH);
        add(panelHeader, BorderLayout.NORTH);

        // --- PANEL CENTRAL CON DIVISIÓN (CONTROLES A LA IZQUIERDA, ESTADO Y CONSOLA A LA DERECHA) ---
        JPanel panelContenido = new JPanel(new GridLayout(1, 2, 10, 10));
        panelContenido.setBorder(new EmptyBorder(10, 15, 10, 15));

        // LADO IZQUIERDO: Formulario Interactivo de Patrones
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));

        // 1. Datos del Pedido & Canales (Abstract Factory + Factory Method)
        JPanel pnlCanal = new JPanel(new GridLayout(3, 2, 6, 6));
        pnlCanal.setBorder(new TitledBorder("1. Cliente y Canales [Abstract Factory / Factory Method]"));
        pnlCanal.add(new JLabel("Nombre Cliente:"));
        txtCliente = new JTextField("Lincoln Vega Hidalgo");
        pnlCanal.add(txtCliente);

        pnlCanal.add(new JLabel("Canal de Venta:"));
        cmbCanal = new JComboBox<>(new String[]{"DELIVERY", "SALON", "PARA_LLEVAR"});
        pnlCanal.add(cmbCanal);

        pnlCanal.add(new JLabel("Detalle (Dirección / Mesa):"));
        txtDetalleCanal = new JTextField("Av. Central 456, Lima");
        pnlCanal.add(txtDetalleCanal);
        panelIzquierdo.add(pnlCanal);

        // 2. Menú, Decoradores y Combos (Composite + Decorator)
        JPanel pnlMenu = new JPanel(new GridLayout(5, 1, 4, 4));
        pnlMenu.setBorder(new TitledBorder("2. Menús y Personalización [Composite & Decorator]"));
        
        JPanel pnlPlatoBase = new JPanel(new BorderLayout(5, 5));
        pnlPlatoBase.add(new JLabel("Plato Base:"), BorderLayout.WEST);
        cmbPlato = new JComboBox<>(new String[]{
            "Hamburguesa Artesanal (S/ 22.00)",
            "Pollo a la Brasa Personal (S/ 24.50)",
            "Lomo Saltado Criollo (S/ 28.00)"
        });
        pnlPlatoBase.add(cmbPlato, BorderLayout.CENTER);
        pnlMenu.add(pnlPlatoBase);

        chkExtraQueso = new JCheckBox("🧀 Extra Queso Cheddar (+ S/ 3.00) [Decorator]");
        chkPapasExtra = new JCheckBox("🍟 Porción Papas Extra (+ S/ 4.50) [Decorator]");
        chkSinCebolla = new JCheckBox("🧅 Sin Cebolla (S/ 0.00) [Decorator]");
        chkCombo = new JCheckBox("📦 Convertir en Combo Familiar (-10% desc. + Chicha S/ 6.00) [Composite]");
        chkCombo.setSelected(true);

        pnlMenu.add(chkExtraQueso);
        pnlMenu.add(chkPapasExtra);
        pnlMenu.add(chkSinCebolla);
        pnlMenu.add(chkCombo);
        panelIzquierdo.add(pnlMenu);

        // 3. Pagos y Almacén (Adapter + Proxy)
        JPanel pnlPagoAlmacen = new JPanel(new GridLayout(2, 2, 6, 6));
        pnlPagoAlmacen.setBorder(new TitledBorder("3. Pasarela de Pago & Seguridad [Adapter / Proxy]"));
        
        JPanel pnlMetodoPago = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        rbtnYape = new JRadioButton("📱 Yape", true);
        rbtnTarjeta = new JRadioButton("💳 Tarjeta", false);
        ButtonGroup bgPago = new ButtonGroup();
        bgPago.add(rbtnYape);
        bgPago.add(rbtnTarjeta);
        pnlMetodoPago.add(rbtnYape);
        pnlMetodoPago.add(rbtnTarjeta);

        pnlPagoAlmacen.add(new JLabel("Método Pago [Adapter]:"));
        pnlPagoAlmacen.add(pnlMetodoPago);

        pnlPagoAlmacen.add(new JLabel("Rol Almacén [Proxy]:"));
        cmbRolProxy = new JComboBox<>(new String[]{"COCINA (Acceso Total)", "MOZO (Solo Consulta)", "ADMIN (Acceso Total)"});
        pnlPagoAlmacen.add(cmbRolProxy);
        panelIzquierdo.add(pnlPagoAlmacen);

        // 4. Botones de Control y Acciones Interactivas
        JPanel pnlBotones = new JPanel(new GridLayout(3, 2, 6, 6));
        pnlBotones.setBorder(new TitledBorder("4. Acciones y Comandos [Builder / Command / Memento / Prototype]"));

        JButton btnClonar = new JButton("📋 Cargar Prototipo [Prototype]");
        btnClonar.addActionListener(e -> accionCargarPrototipo());

        JButton btnConstruir = new JButton("🔨 Armar Pedido [Builder]");
        btnConstruir.setBackground(new Color(237, 242, 247));
        btnConstruir.addActionListener(e -> accionConstruirPedido());

        JButton btnSnapshot = new JButton("💾 Guardar Snapshot [Memento]");
        btnSnapshot.addActionListener(e -> accionGuardarSnapshot());

        JButton btnUndo = new JButton("↩️ Deshacer Último [Undo]");
        btnUndo.addActionListener(e -> accionDeshacer());

        JButton btnEnviarCocina = new JButton("👨‍🍳 Enviar a Cocina [Command]");
        btnEnviarCocina.addActionListener(e -> accionEnviarCocina());

        JButton btnAvanzarEstado = new JButton("⏩ Avanzar Estado [State]");
        btnAvanzarEstado.setBackground(new Color(254, 235, 200));
        btnAvanzarEstado.addActionListener(e -> accionAvanzarEstado());

        pnlBotones.add(btnClonar);
        pnlBotones.add(btnConstruir);
        pnlBotones.add(btnSnapshot);
        pnlBotones.add(btnUndo);
        pnlBotones.add(btnEnviarCocina);
        pnlBotones.add(btnAvanzarEstado);
        panelIzquierdo.add(pnlBotones);

        // Botón Maestro Facade
        JButton btnFacadeAuto = new JButton("⚡ EJECUTAR DEMOSTRACIÓN COMPLETA AUTOMÁTICA [15 PATRONES - FACADE]");
        btnFacadeAuto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFacadeAuto.setBackground(new Color(43, 108, 176));
        btnFacadeAuto.setForeground(Color.WHITE);
        btnFacadeAuto.setFocusPainted(false);
        btnFacadeAuto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnFacadeAuto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnFacadeAuto.addActionListener(e -> accionEjecutarFacadeCompleta());
        panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 8)));
        panelIzquierdo.add(btnFacadeAuto);

        panelContenido.add(panelIzquierdo);

        // LADO DERECHO: Visualización del Ciclo de Vida (STATE + OBSERVER) y Consola en Vivo
        JPanel panelDerecho = new JPanel(new BorderLayout(8, 8));

        // Panel de Monitores y Estado (State visual)
        JPanel pnlEstadoVisual = new JPanel(new GridLayout(2, 1, 4, 4));
        pnlEstadoVisual.setBorder(new TitledBorder("Monitor en Vivo del Ciclo de Vida [State & Observer]"));

        JPanel pnlPasos = new JPanel(new GridLayout(1, 4, 4, 4));
        lblEstadoRegistrado = crearLabelEstado("1. REGISTRADO");
        lblEstadoCocina = crearLabelEstado("2. EN COCINA");
        lblEstadoListo = crearLabelEstado("3. LISTO");
        lblEstadoEntregado = crearLabelEstado("4. ENTREGADO");

        pnlPasos.add(lblEstadoRegistrado);
        pnlPasos.add(lblEstadoCocina);
        pnlPasos.add(lblEstadoListo);
        pnlPasos.add(lblEstadoEntregado);
        pnlEstadoVisual.add(pnlPasos);

        JLabel lblInfoObserver = new JLabel("📡 Monitores activos: CocinaMonitor, CajaMonitor, DeliveryRastreadorGPS");
        lblInfoObserver.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfoObserver.setHorizontalAlignment(SwingConstants.CENTER);
        pnlEstadoVisual.add(lblInfoObserver);

        panelDerecho.add(pnlEstadoVisual, BorderLayout.NORTH);

        // Consola de Salida en Tiempo Real
        JPanel pnlConsolaWrapper = new JPanel(new BorderLayout());
        pnlConsolaWrapper.setBorder(new TitledBorder("Registro de Ejecución de Patrones en Tiempo Real"));

        txtConsola = new JTextArea();
        txtConsola.setBackground(new Color(24, 24, 27)); // Fondo oscuro terminal
        txtConsola.setForeground(new Color(74, 222, 128)); // Texto verde lima estilo consola
        txtConsola.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtConsola.setEditable(false);
        txtConsola.setMargin(new Insets(8, 8, 8, 8));

        JScrollPane scrollConsola = new JScrollPane(txtConsola);
        pnlConsolaWrapper.add(scrollConsola, BorderLayout.CENTER);

        JButton btnLimpiar = new JButton("🧹 Limpiar Registro");
        btnLimpiar.addActionListener(e -> txtConsola.setText(""));
        pnlConsolaWrapper.add(btnLimpiar, BorderLayout.SOUTH);

        panelDerecho.add(pnlConsolaWrapper, BorderLayout.CENTER);
        panelContenido.add(panelDerecho);

        add(panelContenido, BorderLayout.CENTER);
    }

    private JLabel crearLabelEstado(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(226, 232, 240));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        lbl.setPreferredSize(new Dimension(100, 32));
        return lbl;
    }

    private void actualizarIndicadorEstadoVisual(String estado) {
        // Reset
        lblEstadoRegistrado.setBackground(new Color(226, 232, 240));
        lblEstadoRegistrado.setForeground(new Color(100, 116, 139));
        lblEstadoCocina.setBackground(new Color(226, 232, 240));
        lblEstadoCocina.setForeground(new Color(100, 116, 139));
        lblEstadoListo.setBackground(new Color(226, 232, 240));
        lblEstadoListo.setForeground(new Color(100, 116, 139));
        lblEstadoEntregado.setBackground(new Color(226, 232, 240));
        lblEstadoEntregado.setForeground(new Color(100, 116, 139));

        if ("REGISTRADO".equalsIgnoreCase(estado)) {
            lblEstadoRegistrado.setBackground(new Color(59, 130, 246));
            lblEstadoRegistrado.setForeground(Color.WHITE);
        } else if ("EN_COCINA".equalsIgnoreCase(estado)) {
            lblEstadoCocina.setBackground(new Color(245, 158, 11));
            lblEstadoCocina.setForeground(Color.WHITE);
        } else if ("LISTO".equalsIgnoreCase(estado) || "LISTO_PARA_ENTREGA".equalsIgnoreCase(estado)) {
            lblEstadoListo.setBackground(new Color(16, 185, 129));
            lblEstadoListo.setForeground(Color.WHITE);
        } else if ("ENTREGADO".equalsIgnoreCase(estado)) {
            lblEstadoEntregado.setBackground(new Color(34, 197, 94));
            lblEstadoEntregado.setForeground(Color.WHITE);
        }
    }

    /**
     * Redirige System.out y System.err a la ventana Swing además de la consola física.
     */
    private void redirigirConsolaASwing() {
        PrintStream originalOut = System.out;
        OutputStream outStream = new OutputStream() {
            private StringBuilder sb = new StringBuilder();

            @Override
            public void write(int b) {
                if (b == '\n') {
                    String linea = sb.toString();
                    sb.setLength(0);
                    originalOut.println(linea);
                    SwingUtilities.invokeLater(() -> {
                        txtConsola.append(linea + "\n");
                        txtConsola.setCaretPosition(txtConsola.getDocument().getLength());
                    });
                } else {
                    sb.append((char) b);
                }
            }
        };
        System.setOut(new PrintStream(outStream, true));
    }

    // --- ACCIONES DE LOS BOTONES DE INTERFAZ ---

    private void accionCargarPrototipo() {
        System.out.println("\n>>> [INTERFAZ] Invocando clonación con PROTOTYPE...");
        Pedido clon = catalogoPlantillas.obtenerClon("EJECUTIVO");
        if (clon != null) {
            this.pedidoActual = clon;
            txtCliente.setText(clon.getCliente());
            System.out.println("✅ Prototipo clonado con éxito: ID #" + clon.getIdPedido() + " (" + clon.getCanalVenta() + ")");
            JOptionPane.showMessageDialog(this, "Plantilla cargada exitosamente mediante el patrón PROTOTYPE.", "Prototype OK", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionConstruirPedido() {
        String cliente = txtCliente.getText().trim();
        if (cliente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese el nombre del cliente.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String canal = (String) cmbCanal.getSelectedItem();
        System.out.println("\n>>> [INTERFAZ] Construyendo pedido mediante BUILDER...");

        // 1. Plato y Decoradores
        int indexPlato = cmbPlato.getSelectedIndex();
        IComponenteMenu plato;
        if (indexPlato == 0) {
            plato = new PlatoSimple("P1", "Hamburguesa Artesanal", 22.00);
        } else if (indexPlato == 1) {
            plato = new PlatoSimple("P2", "Pollo a la Brasa Personal", 24.50);
        } else {
            plato = new PlatoSimple("P3", "Lomo Saltado Criollo", 28.00);
        }

        if (chkSinCebolla.isSelected()) plato = new SinCebollaDecorator(plato);
        if (chkExtraQueso.isSelected()) plato = new ExtraQuesoDecorator(plato);
        if (chkPapasExtra.isSelected()) plato = new PorcionPapasExtraDecorator(plato);

        IComponenteMenu itemFinal;
        if (chkCombo.isSelected()) {
            ComboMenuComposite combo = new ComboMenuComposite("Super Combo Familiar", 0.10);
            combo.agregarComponente(plato);
            combo.agregarComponente(new PlatoSimple("B1", "Chicha Morada 500ml", 6.00));
            itemFinal = combo;
        } else {
            itemFinal = plato;
        }

        // 2. Método de Pago (Adapter)
        IPago metodoPago = rbtnYape.isSelected() 
                ? new YapePaymentAdapter("910286670") 
                : new TarjetaPaymentAdapter("4557880012345678", cliente);

        // 3. Builder
        this.pedidoActual = new PedidoBuilder()
                .conCliente(cliente)
                .enCanal(canal)
                .agregarItem(itemFinal)
                .conPago(metodoPago)
                .conNotas("Pedido desde Interfaz Swing (" + txtDetalleCanal.getText() + ")")
                .build();

        // 4. Registrar Observadores
        this.pedidoActual.registrarObservador(new CocinaMonitorObserver());
        this.pedidoActual.registrarObservador(new CajaMonitorObserver());
        this.pedidoActual.registrarObservador(new DeliveryRastreadorObserver());
        this.pedidoActual.registrarObservador(this); // La propia ventana Swing es observadora

        // 5. Estado inicial
        this.pedidoActual.setEstadoActual(new RegistradoState());
        actualizarIndicadorEstadoVisual("REGISTRADO");

        // 6. Test Proxy con el rol seleccionado
        String rolSeleccionado = ((String) cmbRolProxy.getSelectedItem()).startsWith("MOZO") ? "MOZO" : "COCINA";
        proxyAlmacen.setRolUsuario(rolSeleccionado);
        proxyAlmacen.verificarStock("CARNE_RES", 1);
        proxyAlmacen.descontarStock("CARNE_RES", 1);

        System.out.println("✅ Pedido #" + pedidoActual.getIdPedido() + " construido exitosamente. Total: S/ " + String.format("%.2f", pedidoActual.calcularTotal()));
    }

    private void accionGuardarSnapshot() {
        if (pedidoActual == null) {
            JOptionPane.showMessageDialog(this, "Debe armar un pedido primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        PedidoMemento snapshot = pedidoActual.guardarMemento();
        caretaker.guardarEstado(snapshot);
        JOptionPane.showMessageDialog(this, "Instantánea de estado guardada en Caretaker (MEMENTO).", "Memento OK", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionDeshacer() {
        if (pedidoActual != null && caretaker.puedeDeshacer()) {
            pedidoActual.restaurarMemento(caretaker.deshacer());
            JOptionPane.showMessageDialog(this, "Pedido restaurado a su estado previo exitosamente.", "Undo OK", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No hay estados guardados en el historial para deshacer.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionEnviarCocina() {
        if (pedidoActual == null) {
            JOptionPane.showMessageDialog(this, "Debe armar un pedido primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        System.out.println("\n>>> [INTERFAZ] Ejecutando Enviar a Cocina mediante COMMAND...");
        EnviarCocinaCommand cmd = new EnviarCocinaCommand(pedidoActual);
        gestorComandos.ejecutarComandoDirecto(cmd);

        // Envío de notificaciones mediante BRIDGE
        NotificacionPedidoListo notif = new NotificacionPedidoListo(new WhatsAppImplementador());
        notif.notificar("+51 910286670", "Tu pedido ha sido enviado a preparación.");
    }

    private void accionAvanzarEstado() {
        if (pedidoActual == null) {
            JOptionPane.showMessageDialog(this, "Debe armar un pedido primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        pedidoActual.avanzarEstado();
    }

    private void accionEjecutarFacadeCompleta() {
        String cliente = txtCliente.getText().trim();
        if (cliente.isEmpty()) cliente = "Lincoln Vega Hidalgo";
        String canal = (String) cmbCanal.getSelectedItem();

        txtConsola.setText("");
        System.out.println("🚀 Iniciando Demostración Integral de los 15 Patrones con Facade...\n");
        this.pedidoActual = facade.procesarFlujoCompletoRestaurante(cliente, canal);
        if (this.pedidoActual != null) {
            this.pedidoActual.registrarObservador(this);
            actualizarIndicadorEstadoVisual("ENTREGADO");
        }
    }

    // --- IMPLEMENTACIÓN DEL PATRÓN OBSERVER EN LA GUI ---
    @Override
    public void actualizar(String idPedido, String estadoAnterior, String nuevoEstado, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            actualizarIndicadorEstadoVisual(nuevoEstado);
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}
