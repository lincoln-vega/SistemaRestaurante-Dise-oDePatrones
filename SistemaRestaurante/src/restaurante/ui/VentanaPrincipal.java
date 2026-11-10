package restaurante.ui;

import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import restaurante.config.LocalStorageDB;
import restaurante.config.LocalStorageDB.RegistroPedidoLocalStorage;
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
import restaurante.patterns.creational.builder.PedidoBuilder;
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
 * Interfaz Grafica Moderna e Intuitiva - Gourmet Restaurant POS
 * Botones con alto contraste visual (anti-aliasing personalizado),
 * persistencia LocalStorage (JSON) y demostracion practica de los 15 Patrones GOF.
 * Responsable Tecnico: Lincoln Vega Hidalgo
 */
public class VentanaPrincipal extends JFrame implements IObservadorPedido {

    // Paleta de Colores Moderna (Tailwind-inspired)
    private static final Color COLOR_PRIMARY = new Color(26, 54, 93);       // Navy profundo
    private static final Color COLOR_GREEN = new Color(22, 163, 74);        // Verde esmeralda para confirmar
    private static final Color COLOR_BLUE = new Color(37, 99, 235);         // Azul accion
    private static final Color COLOR_AMBER = new Color(217, 119, 6);        // Ambar para estados
    private static final Color COLOR_RED = new Color(220, 38, 38);          // Rojo cancelar
    private static final Color COLOR_GRAY_BG = new Color(248, 250, 252);    // Fondo suave
    private static final Color COLOR_CARD_BG = new Color(255, 255, 255);    // Tarjetas blancas
    private static final Color COLOR_BORDER = new Color(226, 232, 240);     // Bordes sutiles

    // Negocio y Patrones
    private LocalStorageDB storageDB;
    private RestauranteFacade facade;
    private CatalogoPlantillas catalogoPlantillas;
    private HistorialPedidoCaretaker caretaker;
    private GestorComandosRestaurante gestorComandos;
    private AlmacenProxySeguro proxyAlmacen;

    // Componentes - POS
    private JTextField txtCliente;
    private JComboBox<String> cmbCanal;
    private JTextField txtDetalleCanal;
    private JComboBox<String> cmbPlatos;
    private JCheckBox chkQueso;
    private JCheckBox chkPapas;
    private JCheckBox chkSinCebolla;
    private JCheckBox chkCombo;
    private DefaultTableModel modeloCarrito;
    private JTable tblCarrito;
    private JLabel lblSubtotal;
    private JLabel lblDescuento;
    private JLabel lblTotalPagar;
    private JRadioButton rbtnYape;
    private JRadioButton rbtnTarjeta;
    private List<IComponenteMenu> carritoItems;
    private PedidoMemento snapshotBorrador;

    // Componentes - KDS
    private DefaultTableModel modeloPedidosKDS;
    private JTable tblPedidosKDS;
    private JLabel lblEstadoKDS;

    // Componentes - Almacen
    private JComboBox<String> cmbRolAlmacen;
    private DefaultTableModel modeloInventario;
    private JTable tblInventario;

    // Componentes - Auditoria
    private JTextArea txtConsolaLogs;

    public VentanaPrincipal() {
        super("Gourmet Manager POS - Sistema de Restaurante (Avance 50% UTP)");
        inicializarNegocio();
        configurarVentana();
        crearInterfazGrafica();
        redirigirSalidaConsola();
        cargarDatosDesdeLocalStorage();
    }

    private void inicializarNegocio() {
        this.storageDB = LocalStorageDB.getInstancia();
        this.facade = new RestauranteFacade();
        this.catalogoPlantillas = new CatalogoPlantillas();
        this.caretaker = new HistorialPedidoCaretaker();
        this.gestorComandos = new GestorComandosRestaurante();
        this.proxyAlmacen = new AlmacenProxySeguro("COCINA");
        this.carritoItems = new ArrayList<>();

        // Registrar plantilla para PROTOTYPE
        Pedido plantillaEjecutiva = new Pedido("PLANT-EJEC", "Cliente Ejecutivo", "SALON");
        plantillaEjecutiva.agregarItem(new PlatoSimple("P-EJEC", "Almuerzo Ejecutivo Completo", 18.00));
        catalogoPlantillas.registrarPlantilla("EJECUTIVO", plantillaEjecutiva);
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 860);
        setMinimumSize(new Dimension(1080, 740));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    /**
     * Fabrica de botones personalizados de alto contraste.
     * Evita el fallo de renderizado blanco sobre blanco de Windows LookAndFeel.
     */
    public static JButton crearBotonEstilizado(String texto, Color bg, Color fg, int altura) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, altura));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void crearInterfazGrafica() {
        // --- HEADER PRINCIPAL ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(14, 24, 14, 24));

        JPanel pnlTitulos = new JPanel(new GridLayout(2, 1, 3, 3));
        pnlTitulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("GOURMET RESTAURANT MANAGER • SISTEMA INTEGRAL POS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("UTP | Diseno de Patrones (Semana 14 - Avance 50%) | Lider Tecnico: Lincoln Vega Hidalgo");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(203, 213, 225));

        pnlTitulos.add(lblTitulo);
        pnlTitulos.add(lblSub);
        header.add(pnlTitulos, BorderLayout.WEST);

        JLabel lblStorageBadge = new JLabel("[BD] Persistencia: LocalStorage JSON (Modo Offline)");
        lblStorageBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStorageBadge.setForeground(new Color(134, 239, 172));
        header.add(lblStorageBadge, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // --- SISTEMA DE PESTANAS (TABBED PANE) ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabs.addTab("PUNTO DE VENTA (POS)", crearPestanaPOS());
        tabs.addTab("COCINA Y PEDIDOS ACTIVOS (KDS)", crearPestanaKDS());
        tabs.addTab("INVENTARIO Y ALMACEN (PROXY)", crearPestanaAlmacen());
        tabs.addTab("INSPECTOR DE PATRONES GOF", crearPestanaAuditoria());

        add(tabs, BorderLayout.CENTER);
    }

    // =========================================================================
    // PESTANA 1: PUNTO DE VENTA (POS)
    // =========================================================================
    private JPanel crearPestanaPOS() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(COLOR_GRAY_BG);
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        // LADO IZQUIERDO: Formulario de Orden
        JPanel pnlIzquierdo = new JPanel();
        pnlIzquierdo.setLayout(new BoxLayout(pnlIzquierdo, BoxLayout.Y_AXIS));
        pnlIzquierdo.setOpaque(false);
        pnlIzquierdo.setPreferredSize(new Dimension(530, 600));

        // 1. Cliente y Canal
        JPanel pnlCliente = crearPanelTarjeta("1. Informacion del Cliente y Canal de Atencion");
        pnlCliente.setLayout(new GridLayout(3, 2, 8, 8));
        pnlCliente.add(new JLabel("Nombre Cliente:"));
        txtCliente = new JTextField("Lincoln Vega Hidalgo");
        pnlCliente.add(txtCliente);

        pnlCliente.add(new JLabel("Canal de Venta:"));
        cmbCanal = new JComboBox<>(new String[]{"DELIVERY", "SALON", "PARA_LLEVAR"});
        cmbCanal.addActionListener(e -> actualizarDetalleCanal());
        pnlCliente.add(cmbCanal);

        pnlCliente.add(new JLabel("Direccion / Mesa:"));
        txtDetalleCanal = new JTextField("Av. Central 456, Lima");
        pnlCliente.add(txtDetalleCanal);
        pnlIzquierdo.add(pnlCliente);
        pnlIzquierdo.add(Box.createRigidArea(new Dimension(0, 10)));

        // 2. Platos y Adicionales
        JPanel pnlPlatos = crearPanelTarjeta("2. Seleccion de Menus y Adicionales [Decorator / Composite]");
        pnlPlatos.setLayout(new GridLayout(6, 1, 6, 6));

        JPanel pnlPlatoCombo = new JPanel(new BorderLayout(8, 8));
        pnlPlatoCombo.setOpaque(false);
        pnlPlatoCombo.add(new JLabel("Plato:"), BorderLayout.WEST);
        cmbPlatos = new JComboBox<>(new String[]{
            "Hamburguesa Artesanal Clasica - S/ 22.00",
            "Pollo a la Brasa (1/4 Personal) - S/ 24.50",
            "Lomo Saltado Criollo Gourmet - S/ 28.00"
        });
        pnlPlatoCombo.add(cmbPlatos, BorderLayout.CENTER);
        pnlPlatos.add(pnlPlatoCombo);

        chkQueso = new JCheckBox("(+) Extra Queso Cheddar (+ S/ 3.00) [Decorator]");
        chkPapas = new JCheckBox("(+) Porcion Papas Fritas Extra (+ S/ 4.50) [Decorator]");
        chkSinCebolla = new JCheckBox("(*) Indicacion Especial: Sin Cebolla (S/ 0.00) [Decorator]");
        chkCombo = new JCheckBox("[Combo] Convertir en Combo Duo (+ Chicha Morada S/ 6.00 y 10% Desc.) [Composite]");
        chkCombo.setSelected(true);

        pnlPlatos.add(chkQueso);
        pnlPlatos.add(chkPapas);
        pnlPlatos.add(chkSinCebolla);
        pnlPlatos.add(chkCombo);

        JButton btnAgregar = crearBotonEstilizado("[+] AGREGAR AL CARRITO", new Color(30, 58, 138), Color.WHITE, 36);
        btnAgregar.addActionListener(e -> accionAgregarAlCarrito());
        pnlPlatos.add(btnAgregar);

        pnlIzquierdo.add(pnlPlatos);
        pnlIzquierdo.add(Box.createRigidArea(new Dimension(0, 10)));

        // 3. Atajos Prototype y Memento
        JPanel pnlAtajos = crearPanelTarjeta("3. Operaciones Rapidas [Prototype & Memento]");
        pnlAtajos.setLayout(new GridLayout(1, 3, 8, 8));

        JButton btnProto = crearBotonEstilizado("Clonar Plantilla", new Color(71, 85, 105), Color.WHITE, 34);
        btnProto.setToolTipText("Patron Prototype: Clona pedido base");
        btnProto.addActionListener(e -> accionClonarPlantilla());

        JButton btnSnapshot = crearBotonEstilizado("Snapshot", new Color(71, 85, 105), Color.WHITE, 34);
        btnSnapshot.setToolTipText("Patron Memento: Guarda estado actual");
        btnSnapshot.addActionListener(e -> accionGuardarBorrador());

        JButton btnUndo = crearBotonEstilizado("Deshacer", new Color(148, 163, 184), new Color(15, 23, 42), 34);
        btnUndo.setToolTipText("Patron Memento: Revierte el ultimo cambio");
        btnUndo.addActionListener(e -> accionRestaurarBorrador());

        pnlAtajos.add(btnProto);
        pnlAtajos.add(btnSnapshot);
        pnlAtajos.add(btnUndo);
        pnlIzquierdo.add(pnlAtajos);

        panel.add(pnlIzquierdo, BorderLayout.WEST);

        // LADO DERECHO: Carrito, Totales y Confirmacion
        JPanel pnlDerecho = crearPanelTarjeta("Detalle del Pedido Actual (Carrito)");
        pnlDerecho.setLayout(new BorderLayout(10, 10));

        modeloCarrito = new DefaultTableModel(new String[]{"Item / Plato", "Detalles y Adicionales", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCarrito = new JTable(modeloCarrito);
        tblCarrito.setRowHeight(28);
        pnlDerecho.add(new JScrollPane(tblCarrito), BorderLayout.CENTER);

        // Panel Inferior de Pago y Total
        JPanel pnlPagoTotal = new JPanel(new BorderLayout(10, 10));
        pnlPagoTotal.setOpaque(false);

        // Metodo de Pago
        JPanel pnlFormaPago = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        pnlFormaPago.setBorder(new TitledBorder("Metodo de Pago [Adapter]"));
        pnlFormaPago.setOpaque(false);

        rbtnYape = new JRadioButton("Yape (BCP)", true);
        rbtnTarjeta = new JRadioButton("Tarjeta Visa/Mastercard", false);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbtnYape);
        bg.add(rbtnTarjeta);
        pnlFormaPago.add(rbtnYape);
        pnlFormaPago.add(rbtnTarjeta);

        JButton btnQuitar = crearBotonEstilizado("[-] Quitar Item", new Color(239, 68, 68), Color.WHITE, 28);
        btnQuitar.addActionListener(e -> accionQuitarItemCarrito());
        pnlFormaPago.add(btnQuitar);

        pnlPagoTotal.add(pnlFormaPago, BorderLayout.NORTH);

        // Tarjeta de Totales
        JPanel pnlTotales = new JPanel(new GridLayout(3, 2, 4, 4));
        pnlTotales.setBackground(new Color(241, 245, 249));
        pnlTotales.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDER, 1), new EmptyBorder(10, 16, 10, 16)));

        pnlTotales.add(new JLabel("Subtotal Platos:"));
        lblSubtotal = new JLabel("S/ 0.00", SwingConstants.RIGHT);
        lblSubtotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlTotales.add(lblSubtotal);

        pnlTotales.add(new JLabel("Recargo Canal / Envio:"));
        lblDescuento = new JLabel("S/ 5.00", SwingConstants.RIGHT);
        lblDescuento.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlTotales.add(lblDescuento);

        JLabel lblTotalEtiqueta = new JLabel("TOTAL A PAGAR:");
        lblTotalEtiqueta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalEtiqueta.setForeground(new Color(15, 23, 42));
        pnlTotales.add(lblTotalEtiqueta);

        lblTotalPagar = new JLabel("S/ 0.00", SwingConstants.RIGHT);
        lblTotalPagar.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalPagar.setForeground(COLOR_GREEN);
        pnlTotales.add(lblTotalPagar);

        pnlPagoTotal.add(pnlTotales, BorderLayout.CENTER);

        // BOTON CONFIRMAR GIGANTE Y VISIBLE (Solucion definitiva a la visibilidad)
        JButton btnConfirmar = crearBotonEstilizado("CONFIRMAR Y GUARDAR PEDIDO (Guardar en LocalStorage)", COLOR_GREEN, Color.WHITE, 48);
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnConfirmar.addActionListener(e -> accionConfirmarYGuardarPedido());
        pnlPagoTotal.add(btnConfirmar, BorderLayout.SOUTH);

        pnlDerecho.add(pnlPagoTotal, BorderLayout.SOUTH);
        panel.add(pnlDerecho, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // PESTANA 2: COCINA Y KDS
    // =========================================================================
    private JPanel crearPestanaKDS() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(COLOR_GRAY_BG);
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        JLabel lblTitulo = new JLabel("COLA DE PEDIDOS EN LOCALSTORAGE (Kitchen Display System)");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_PRIMARY);
        pnlTop.add(lblTitulo, BorderLayout.WEST);

        lblEstadoKDS = new JLabel("Seleccione un pedido en la tabla para avanzar su estado.");
        lblEstadoKDS.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pnlTop.add(lblEstadoKDS, BorderLayout.EAST);
        panel.add(pnlTop, BorderLayout.NORTH);

        modeloPedidosKDS = new DefaultTableModel(new String[]{"ID Pedido", "Cliente", "Canal", "Contenido del Menu", "Total (S/)", "Pago", "Estado Actual"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPedidosKDS = new JTable(modeloPedidosKDS);
        tblPedidosKDS.setRowHeight(32);
        tblPedidosKDS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblPedidosKDS.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String estado = (String) value;
                if ("REGISTRADO".equalsIgnoreCase(estado)) {
                    c.setForeground(new Color(2, 132, 199));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("EN_COCINA".equalsIgnoreCase(estado)) {
                    c.setForeground(new Color(217, 119, 6));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("LISTO".equalsIgnoreCase(estado)) {
                    c.setForeground(new Color(13, 148, 136));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("ENTREGADO".equalsIgnoreCase(estado)) {
                    c.setForeground(new Color(22, 163, 74));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("CANCELADO".equalsIgnoreCase(estado)) {
                    c.setForeground(new Color(220, 38, 38));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
                return c;
            }
        });

        panel.add(new JScrollPane(tblPedidosKDS), BorderLayout.CENTER);

        // Botonera de Control de Estado
        JPanel pnlAcciones = crearPanelTarjeta("Control del Ciclo de Vida del Pedido [State / Command / Observer]");
        pnlAcciones.setLayout(new GridLayout(1, 5, 10, 10));

        JButton btnCocina = crearBotonEstilizado("1. Pasar a Cocina", COLOR_BLUE, Color.WHITE, 40);
        btnCocina.addActionListener(e -> cambiarEstadoPedidoSeleccionado("EN_COCINA"));

        JButton btnListo = crearBotonEstilizado("2. Marcar Listo", COLOR_AMBER, Color.WHITE, 40);
        btnListo.addActionListener(e -> cambiarEstadoPedidoSeleccionado("LISTO"));

        JButton btnEntregar = crearBotonEstilizado("3. Entregar", COLOR_GREEN, Color.WHITE, 40);
        btnEntregar.addActionListener(e -> cambiarEstadoPedidoSeleccionado("ENTREGADO"));

        JButton btnCancelar = crearBotonEstilizado("4. Cancelar Pedido", COLOR_RED, Color.WHITE, 40);
        btnCancelar.addActionListener(e -> cambiarEstadoPedidoSeleccionado("CANCELADO"));

        JButton btnRecargar = crearBotonEstilizado("Recargar Lista", new Color(71, 85, 105), Color.WHITE, 40);
        btnRecargar.addActionListener(e -> cargarDatosDesdeLocalStorage());

        pnlAcciones.add(btnCocina);
        pnlAcciones.add(btnListo);
        pnlAcciones.add(btnEntregar);
        pnlAcciones.add(btnCancelar);
        pnlAcciones.add(btnRecargar);

        panel.add(pnlAcciones, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // PESTANA 3: INVENTARIO Y ALMACEN (PROXY)
    // =========================================================================
    private JPanel crearPestanaAlmacen() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(COLOR_GRAY_BG);
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel pnlRol = crearPanelTarjeta("Control de Acceso y Permisos por Rol [Patron Proxy]");
        pnlRol.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        pnlRol.add(new JLabel("Rol Actual del Usuario:"));

        cmbRolAlmacen = new JComboBox<>(new String[]{
            "MOZO (Solo Consulta - Restringido)",
            "COCINA (Acceso Total a Insumos)",
            "ADMIN (Acceso Total a Inventario)"
        });
        cmbRolAlmacen.addActionListener(e -> {
            String rol = cmbRolAlmacen.getSelectedIndex() == 0 ? "MOZO" : "COCINA";
            proxyAlmacen.setRolUsuario(rol);
            System.out.println("[PROXY ALMACEN] Rol del usuario cambiado a: " + rol);
        });
        pnlRol.add(cmbRolAlmacen);
        panel.add(pnlRol, BorderLayout.NORTH);

        modeloInventario = new DefaultTableModel(new String[]{"Codigo Insumo", "Descripcion", "Stock Disponible", "Unidad", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        modeloInventario.addRow(new Object[]{"CARNE_RES", "Carne Molida Premium 150g", 48, "Porciones", "DISPONIBLE"});
        modeloInventario.addRow(new Object[]{"PAPAS_FRITAS", "Papas Amarillas Pre-cortadas", 95, "Porciones", "DISPONIBLE"});
        modeloInventario.addRow(new Object[]{"GASEOSA_1L", "Bebidas y Refrescos 500ml", 28, "Unidades", "DISPONIBLE"});
        modeloInventario.addRow(new Object[]{"QUESO_CHEDDAR", "Queso Cheddar Tajadas", 40, "Tajadas", "DISPONIBLE"});

        tblInventario = new JTable(modeloInventario);
        tblInventario.setRowHeight(30);
        panel.add(new JScrollPane(tblInventario), BorderLayout.CENTER);

        JPanel pnlAccionesStock = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlAccionesStock.setOpaque(false);

        JButton btnConsultar = crearBotonEstilizado("Consultar Stock (Permitido)", new Color(71, 85, 105), Color.WHITE, 38);
        btnConsultar.addActionListener(e -> {
            int row = tblInventario.getSelectedRow();
            if (row == -1) row = 0;
            String codigo = (String) modeloInventario.getValueAt(row, 0);
            proxyAlmacen.verificarStock(codigo, 1);
            JOptionPane.showMessageDialog(this, "Stock verificado correctamente mediante el PROXY para: " + codigo, "Proxy Consulta OK", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnDescontar = crearBotonEstilizado("Descontar Stock (Protegido por Proxy)", COLOR_PRIMARY, Color.WHITE, 38);
        btnDescontar.addActionListener(e -> {
            int row = tblInventario.getSelectedRow();
            if (row == -1) row = 0;
            String codigo = (String) modeloInventario.getValueAt(row, 0);
            boolean resultado = proxyAlmacen.descontarStock(codigo, 1);
            if (resultado) {
                int stockActual = (int) modeloInventario.getValueAt(row, 2);
                modeloInventario.setValueAt(stockActual - 1, row, 2);
                JOptionPane.showMessageDialog(this, "Stock descontado con exito por el rol autorizado.", "Proxy OK", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "ACCESO DENEGADO POR SEGURIDAD:\nEl rol 'MOZO' no tiene permisos para descontar insumos del almacen.", "Proxy Security Alert", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlAccionesStock.add(btnConsultar);
        pnlAccionesStock.add(btnDescontar);
        panel.add(pnlAccionesStock, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================================================
    // PESTANA 4: AUDITORIA DE PATRONES
    // =========================================================================
    private JPanel crearPestanaAuditoria() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(COLOR_GRAY_BG);
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel pnlGrid = crearPanelTarjeta("Matriz de los 15 Patrones de Diseno GOF (Verificados)");
        pnlGrid.setLayout(new GridLayout(3, 5, 8, 8));

        String[] nombresPatrones = {
            "1. Singleton (ConexionDB)",
            "2. Abstract Factory (Canales)",
            "3. Factory Method (Pedidos)",
            "4. Prototype (Plantillas)",
            "5. Composite (Combos)",
            "6. Decorator (Extras/Queso)",
            "7. Builder (PedidoBuilder)",
            "8. Proxy (Seguridad Almacen)",
            "9. Memento (Undo/Snapshot)",
            "10. Adapter (Yape/Tarjeta)",
            "11. Bridge (Notificaciones)",
            "12. Command (Ordenes Cocina)",
            "13. State (Ciclo de Vida)",
            "14. Observer (Monitores GPS)",
            "15. Facade (Fachada Maestra)"
        };

        for (String p : nombresPatrones) {
            JCheckBox chk = new JCheckBox(p, true);
            chk.setEnabled(false);
            chk.setFont(new Font("Segoe UI", Font.BOLD, 11));
            chk.setForeground(new Color(22, 101, 52));
            chk.setOpaque(false);
            pnlGrid.add(chk);
        }
        panel.add(pnlGrid, BorderLayout.NORTH);

        JPanel pnlLogs = crearPanelTarjeta("Trazabilidad y Salida Tecnica en Tiempo Real");
        pnlLogs.setLayout(new BorderLayout(8, 8));

        txtConsolaLogs = new JTextArea();
        txtConsolaLogs.setBackground(new Color(24, 24, 27));
        txtConsolaLogs.setForeground(new Color(74, 222, 128));
        txtConsolaLogs.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtConsolaLogs.setEditable(false);
        txtConsolaLogs.setMargin(new Insets(8, 8, 8, 8));
        pnlLogs.add(new JScrollPane(txtConsolaLogs), BorderLayout.CENTER);

        JPanel pnlBotonesLogs = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pnlBotonesLogs.setOpaque(false);

        JButton btnFacade = crearBotonEstilizado("EJECUTAR DEMO INTEGRAL DE 15 PATRONES [FACADE]", COLOR_BLUE, Color.WHITE, 38);
        btnFacade.addActionListener(e -> {
            txtConsolaLogs.setText("");
            facade.procesarFlujoCompletoRestaurante(txtCliente.getText(), (String) cmbCanal.getSelectedItem());
            cargarDatosDesdeLocalStorage();
        });

        JButton btnLimpiar = crearBotonEstilizado("Limpiar Consola", new Color(148, 163, 184), new Color(15, 23, 42), 38);
        btnLimpiar.addActionListener(e -> txtConsolaLogs.setText(""));

        pnlBotonesLogs.add(btnFacade);
        pnlBotonesLogs.add(btnLimpiar);
        pnlLogs.add(pnlBotonesLogs, BorderLayout.SOUTH);

        panel.add(pnlLogs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelTarjeta(String titulo) {
        JPanel card = new JPanel();
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new CompoundBorder(new TitledBorder(titulo), new EmptyBorder(8, 12, 10, 12))
        ));
        return card;
    }

    // =========================================================================
    // EVENTOS Y ACCIONES
    // =========================================================================

    private void actualizarDetalleCanal() {
        String canal = (String) cmbCanal.getSelectedItem();
        if ("DELIVERY".equalsIgnoreCase(canal)) {
            txtDetalleCanal.setText("Av. Central 456, Lima");
            lblDescuento.setText("S/ 5.00");
        } else if ("SALON".equalsIgnoreCase(canal)) {
            txtDetalleCanal.setText("Mesa #12 (Salon Principal)");
            lblDescuento.setText("S/ 0.00");
        } else {
            txtDetalleCanal.setText("Retiro en Barra Principal");
            lblDescuento.setText("S/ 0.00");
        }
        recalcularTotales();
    }

    private void accionAgregarAlCarrito() {
        int idx = cmbPlatos.getSelectedIndex();
        IComponenteMenu plato;
        if (idx == 0) {
            plato = new PlatoSimple("P1", "Hamburguesa Artesanal", 22.00);
        } else if (idx == 1) {
            plato = new PlatoSimple("P2", "Pollo a la Brasa (1/4)", 24.50);
        } else {
            plato = new PlatoSimple("P3", "Lomo Saltado Criollo", 28.00);
        }

        StringBuilder extrasStr = new StringBuilder();
        if (chkSinCebolla.isSelected()) {
            plato = new SinCebollaDecorator(plato);
            extrasStr.append("Sin Cebolla; ");
        }
        if (chkQueso.isSelected()) {
            plato = new ExtraQuesoDecorator(plato);
            extrasStr.append("Extra Queso; ");
        }
        if (chkPapas.isSelected()) {
            plato = new PorcionPapasExtraDecorator(plato);
            extrasStr.append("Papas Extra; ");
        }

        IComponenteMenu itemFinal;
        if (chkCombo.isSelected()) {
            ComboMenuComposite combo = new ComboMenuComposite("Combo " + plato.getNombre(), 0.10);
            combo.agregarComponente(plato);
            combo.agregarComponente(new PlatoSimple("B1", "Chicha Morada 500ml", 6.00));
            itemFinal = combo;
            extrasStr.append("Combo + Chicha Morada (-10% Desc.)");
        } else {
            itemFinal = plato;
        }

        carritoItems.add(itemFinal);
        modeloCarrito.addRow(new Object[]{
            itemFinal.getNombre(),
            extrasStr.length() > 0 ? extrasStr.toString() : "Estandar",
            String.format("S/ %.2f", itemFinal.getPrecio())
        });

        recalcularTotales();
        System.out.println("[CARRITO] Item agregado: " + itemFinal.getNombre() + " (S/ " + String.format("%.2f", itemFinal.getPrecio()) + ")");
    }

    private void accionQuitarItemCarrito() {
        int row = tblCarrito.getSelectedRow();
        if (row != -1) {
            carritoItems.remove(row);
            modeloCarrito.removeRow(row);
            recalcularTotales();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un item del carrito para quitar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void recalcularTotales() {
        double subtotal = 0;
        for (IComponenteMenu item : carritoItems) {
            subtotal += item.getPrecio();
        }
        double recargo = "DELIVERY".equalsIgnoreCase((String) cmbCanal.getSelectedItem()) ? 5.00 : 0.00;
        double total = subtotal + recargo;

        lblSubtotal.setText(String.format("S/ %.2f", subtotal));
        lblDescuento.setText(String.format("S/ %.2f", recargo));
        lblTotalPagar.setText(String.format("S/ %.2f", total));
    }

    private void accionClonarPlantilla() {
        Pedido clon = catalogoPlantillas.obtenerClon("EJECUTIVO");
        if (clon != null) {
            txtCliente.setText(clon.getCliente());
            carritoItems.clear();
            modeloCarrito.setRowCount(0);
            for (IComponenteMenu it : clon.getItems()) {
                carritoItems.add(it);
                modeloCarrito.addRow(new Object[]{it.getNombre(), "Plantilla Preconfigurada [Prototype]", String.format("S/ %.2f", it.getPrecio())});
            }
            recalcularTotales();
            System.out.println("[PROTOTYPE] Pedido clonado y cargado al carrito.");
            JOptionPane.showMessageDialog(this, "Plantilla cargada exitosamente mediante el patron PROTOTYPE.", "Prototype OK", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionGuardarBorrador() {
        if (carritoItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un plato al carrito para guardar snapshot.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Pedido pTemp = new Pedido("BORRADOR", txtCliente.getText(), (String) cmbCanal.getSelectedItem());
        pTemp.setItems(new ArrayList<>(carritoItems));
        this.snapshotBorrador = pTemp.guardarMemento();
        caretaker.guardarEstado(snapshotBorrador);
        JOptionPane.showMessageDialog(this, "Borrador guardado en memoria con MEMENTO.", "Memento OK", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionRestaurarBorrador() {
        if (caretaker.puedeDeshacer()) {
            PedidoMemento snap = caretaker.deshacer();
            carritoItems.clear();
            modeloCarrito.setRowCount(0);
            for (IComponenteMenu it : snap.getItemsSnapshot()) {
                carritoItems.add(it);
                modeloCarrito.addRow(new Object[]{it.getNombre(), "Restaurado con Memento", String.format("S/ %.2f", it.getPrecio())});
            }
            recalcularTotales();
            JOptionPane.showMessageDialog(this, "Carrito restaurado exitosamente a su estado previo (Undo).", "Memento Undo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No hay estados guardados para deshacer.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionConfirmarYGuardarPedido() {
        if (carritoItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito esta vacio. Agregue platos antes de confirmar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cliente = txtCliente.getText().trim();
        String canal = (String) cmbCanal.getSelectedItem();
        double recargo = "DELIVERY".equalsIgnoreCase(canal) ? 5.00 : 0.00;
        double subtotal = 0;
        StringBuilder descripcionItems = new StringBuilder();

        for (IComponenteMenu it : carritoItems) {
            subtotal += it.getPrecio();
            descripcionItems.append(it.getNombre()).append("; ");
        }
        double totalFinal = subtotal + recargo;

        // 1. Validar Stock con PROXY
        boolean stockOk = proxyAlmacen.verificarStock("CARNE_RES", 1);
        if (!stockOk) {
            JOptionPane.showMessageDialog(this, "No hay stock suficiente para procesar el pedido.", "Almacen", JOptionPane.ERROR_MESSAGE);
            return;
        }
        proxyAlmacen.descontarStock("CARNE_RES", 1);

        // 2. Procesar Pago con ADAPTER
        String metodoPagoNombre = rbtnYape.isSelected() ? "Yape (910286670)" : "Tarjeta Visa/MC (**** 5678)";
        IPago pago = rbtnYape.isSelected() ? new YapePaymentAdapter("910286670") : new TarjetaPaymentAdapter("4557880012345678", cliente);
        pago.procesarPago(totalFinal, "ORD-" + System.currentTimeMillis() % 10000);

        // 3. Notificacion con BRIDGE
        NotificacionComprobantePago notif = new NotificacionComprobantePago(new EmailImplementador());
        notif.notificar(cliente.toLowerCase().replace(" ", ".") + "@ejemplo.com", "Monto S/ " + String.format("%.2f", totalFinal));

        // 4. Guardar en LocalStorage
        String idPedido = "PED-" + (System.currentTimeMillis() % 10000);
        storageDB.guardarPedido(idPedido, cliente, canal, descripcionItems.toString(), totalFinal, metodoPagoNombre, "REGISTRADO");

        // 5. Limpiar Carrito y Notificar
        carritoItems.clear();
        modeloCarrito.setRowCount(0);
        recalcularTotales();
        cargarDatosDesdeLocalStorage();

        JOptionPane.showMessageDialog(this, "Pedido #" + idPedido + " registrado y guardado exitosamente en LocalStorage.", "Pedido Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cambiarEstadoPedidoSeleccionado(String nuevoEstado) {
        int row = tblPedidosKDS.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido de la tabla de Cocina/KDS.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idPedido = (String) modeloPedidosKDS.getValueAt(row, 0);
        String estadoAnterior = (String) modeloPedidosKDS.getValueAt(row, 6);

        Pedido pedidoDummy = new Pedido(idPedido, (String) modeloPedidosKDS.getValueAt(row, 1), (String) modeloPedidosKDS.getValueAt(row, 2));
        pedidoDummy.registrarObservador(new CocinaMonitorObserver());
        pedidoDummy.registrarObservador(new CajaMonitorObserver());
        pedidoDummy.registrarObservador(new DeliveryRastreadorObserver());
        pedidoDummy.registrarObservador(this);

        if ("EN_COCINA".equalsIgnoreCase(nuevoEstado)) {
            EnviarCocinaCommand cmd = new EnviarCocinaCommand(pedidoDummy);
            gestorComandos.ejecutarComandoDirecto(cmd);
        } else if ("CANCELADO".equalsIgnoreCase(nuevoEstado)) {
            CancelarPedidoCommand cmdCancel = new CancelarPedidoCommand(pedidoDummy);
            gestorComandos.ejecutarComandoDirecto(cmdCancel);
        }

        storageDB.actualizarEstado(idPedido, nuevoEstado);
        cargarDatosDesdeLocalStorage();

        if ("LISTO".equalsIgnoreCase(nuevoEstado)) {
            NotificacionPedidoListo notifListo = new NotificacionPedidoListo(new WhatsAppImplementador());
            notifListo.notificar("+51 910286670", "Tu pedido #" + idPedido + " esta listo para despacho.");
        }

        lblEstadoKDS.setText("Pedido #" + idPedido + " cambio de " + estadoAnterior + " a " + nuevoEstado);
    }

    private void cargarDatosDesdeLocalStorage() {
        List<RegistroPedidoLocalStorage> pedidos = storageDB.listarPedidos();
        modeloPedidosKDS.setRowCount(0);
        for (RegistroPedidoLocalStorage p : pedidos) {
            modeloPedidosKDS.addRow(new Object[]{
                p.getId(),
                p.getCliente(),
                p.getCanal(),
                p.getItems(),
                String.format("%.2f", p.getTotal()),
                p.getPago(),
                p.getEstado()
            });
        }
    }

    private void redirigirSalidaConsola() {
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
                        if (txtConsolaLogs != null) {
                            txtConsolaLogs.append(linea + "\n");
                            txtConsolaLogs.setCaretPosition(txtConsolaLogs.getDocument().getLength());
                        }
                    });
                } else {
                    sb.append((char) b);
                }
            }
        };
        System.setOut(new PrintStream(outStream, true));
    }

    @Override
    public void actualizar(String idPedido, String estadoAnterior, String nuevoEstado, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            cargarDatosDesdeLocalStorage();
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
