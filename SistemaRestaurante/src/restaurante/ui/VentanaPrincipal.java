package restaurante.ui;

import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
import restaurante.patterns.behavioral.state.EnCocinaState;
import restaurante.patterns.behavioral.state.EntregadoState;
import restaurante.patterns.behavioral.state.ListoState;
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
 * Interfaz Gráfica Profesional - Sistema de Restaurante Gourmet POS
 * Integra persistencia LocalStorage (JSON), gestión interactiva de pedidos en tiempo real
 * y demostración práctica de los 15 Patrones de Diseño GOF.
 * Responsable Técnico: Lincoln Vega Hidalgo
 */
public class VentanaPrincipal extends JFrame implements IObservadorPedido {

    // Capa de Negocio y Patrones
    private LocalStorageDB storageDB;
    private RestauranteFacade facade;
    private CatalogoPlantillas catalogoPlantillas;
    private HistorialPedidoCaretaker caretaker;
    private GestorComandosRestaurante gestorComandos;
    private AlmacenProxySeguro proxyAlmacen;

    // Componentes - Pestaña 1 (Punto de Venta / Carrito)
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

    // Componentes - Pestaña 2 (Cocina & KDS)
    private DefaultTableModel modeloPedidosKDS;
    private JTable tblPedidosKDS;
    private JLabel lblEstadoKDS;

    // Componentes - Pestaña 3 (Almacén e Inventario - Proxy)
    private JComboBox<String> cmbRolAlmacen;
    private DefaultTableModel modeloInventario;
    private JTable tblInventario;

    // Componentes - Pestaña 4 (Auditoría & Consola)
    private JTextArea txtConsolaLogs;
    private JCheckBox[] chkPatronesVerificados;

    public VentanaPrincipal() {
        super("Restaurante Gourmet POS - 15 Patrones de Diseño (Avance 50% UTP)");
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
        setSize(1240, 840);
        setMinimumSize(new Dimension(1050, 720));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void crearInterfazGrafica() {
        // --- HEADER PRINCIPAL ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(24, 43, 73));
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JPanel pnlTitulos = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("🍽️ GOURMET RESTAURANT MANAGER • SISTEMA INTEGRAL POS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("UTP | Diseño de Patrones (Semana 14 - Avance 50%) | Líder Técnico: Lincoln Vega Hidalgo");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(203, 213, 225));

        pnlTitulos.add(lblTitulo);
        pnlTitulos.add(lblSub);
        header.add(pnlTitulos, BorderLayout.WEST);

        JLabel lblStorageBadge = new JLabel("💾 Persistencia: LocalStorage JSON (Modo Simulado de BD)");
        lblStorageBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStorageBadge.setForeground(new Color(134, 239, 172));
        header.add(lblStorageBadge, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // --- SISTEMA DE PESTAÑAS (TABBED PANE) ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabs.addTab("🛒 Punto de Venta (POS)", crearPestanaPOS());
        tabs.addTab("👨‍🍳 Cocina & Pedidos Activos (KDS)", crearPestanaKDS());
        tabs.addTab("📦 Inventario & Stock (Proxy)", crearPestanaAlmacen());
        tabs.addTab("🧠 Inspector de Patrones GOF", crearPestanaAuditoria());

        add(tabs, BorderLayout.CENTER);
    }

    // =========================================================================
    // PESTAÑA 1: PUNTO DE VENTA (TOMA DE PEDIDOS)
    // =========================================================================
    private JPanel crearPestanaPOS() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));

        // SPLIT: Lado Izquierdo (Selección) vs Lado Derecho (Carrito & Totales)
        JPanel pnlIzquierdo = new JPanel();
        pnlIzquierdo.setLayout(new BoxLayout(pnlIzquierdo, BoxLayout.Y_AXIS));
        pnlIzquierdo.setPreferredSize(new Dimension(540, 600));

        // 1. Datos del Cliente y Canal
        JPanel pnlCliente = new JPanel(new GridLayout(3, 2, 8, 8));
        pnlCliente.setBorder(new TitledBorder("1. Datos del Cliente & Canal [Factory Method / Abstract Factory]"));
        pnlCliente.add(new JLabel("Cliente:"));
        txtCliente = new JTextField("Lincoln Vega Hidalgo");
        pnlCliente.add(txtCliente);

        pnlCliente.add(new JLabel("Canal de Venta:"));
        cmbCanal = new JComboBox<>(new String[]{"DELIVERY", "SALON", "PARA_LLEVAR"});
        cmbCanal.addActionListener(e -> actualizarDetalleCanal());
        pnlCliente.add(cmbCanal);

        pnlCliente.add(new JLabel("Dirección / Mesa:"));
        txtDetalleCanal = new JTextField("Av. Central 456, Lima");
        pnlCliente.add(txtDetalleCanal);
        pnlIzquierdo.add(pnlCliente);
        pnlIzquierdo.add(Box.createRigidArea(new Dimension(0, 8)));

        // 2. Selección de Platos y Decoradores
        JPanel pnlPlatos = new JPanel(new GridLayout(6, 1, 4, 4));
        pnlPlatos.setBorder(new TitledBorder("2. Menú y Adicionales [Composite & Decorator]"));

        JPanel pnlComboPlato = new JPanel(new BorderLayout(6, 6));
        pnlComboPlato.add(new JLabel("Plato Principal:"), BorderLayout.WEST);
        cmbPlatos = new JComboBox<>(new String[]{
            "Hamburguesa Artesanal Clásica - S/ 22.00",
            "Pollo a la Brasa (1/4 Personal) - S/ 24.50",
            "Lomo Saltado Criollo Gourmet - S/ 28.00"
        });
        pnlComboPlato.add(cmbPlatos, BorderLayout.CENTER);
        pnlPlatos.add(pnlComboPlato);

        chkQueso = new JCheckBox("🧀 Extra Queso Cheddar (+ S/ 3.00) [ExtraQuesoDecorator]");
        chkPapas = new JCheckBox("🍟 Porción Papas Extra (+ S/ 4.50) [PorcionPapasExtraDecorator]");
        chkSinCebolla = new JCheckBox("🧅 Indicación: Sin Cebolla (S/ 0.00) [SinCebollaDecorator]");
        chkCombo = new JCheckBox("📦 Convertir en Combo Dúo (Incluye Chicha Morada S/ 6.00 y 10% Desc.) [Composite]");
        chkCombo.setSelected(true);

        pnlPlatos.add(chkQueso);
        pnlPlatos.add(chkPapas);
        pnlPlatos.add(chkSinCebolla);
        pnlPlatos.add(chkCombo);

        JButton btnAgregarItem = new JButton("➕ AGREGAR AL CARRITO");
        btnAgregarItem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAgregarItem.setBackground(new Color(224, 231, 255));
        btnAgregarItem.addActionListener(e -> accionAgregarAlCarrito());
        pnlPlatos.add(btnAgregarItem);

        pnlIzquierdo.add(pnlPlatos);
        pnlIzquierdo.add(Box.createRigidArea(new Dimension(0, 8)));

        // 3. Prototipos & Historial (Memento / Prototype)
        JPanel pnlHerramientas = new JPanel(new GridLayout(1, 3, 6, 6));
        pnlHerramientas.setBorder(new TitledBorder("3. Herramientas Rápidas [Prototype & Memento]"));

        JButton btnProto = new JButton("📋 Clonar Plantilla");
        btnProto.setToolTipText("Patrón Prototype: Clona pedido preconfigurado");
        btnProto.addActionListener(e -> accionClonarPlantilla());

        JButton btnSnapshot = new JButton("💾 Snapshot");
        btnSnapshot.setToolTipText("Patrón Memento: Guarda estado del borrador");
        btnSnapshot.addActionListener(e -> accionGuardarBorrador());

        JButton btnUndo = new JButton("↩️ Deshacer");
        btnUndo.setToolTipText("Patrón Memento: Revierte al estado previo");
        btnUndo.addActionListener(e -> accionRestaurarBorrador());

        pnlHerramientas.add(btnProto);
        pnlHerramientas.add(btnSnapshot);
        pnlHerramientas.add(btnUndo);
        pnlIzquierdo.add(pnlHerramientas);

        panel.add(pnlIzquierdo, BorderLayout.WEST);

        // LADO DERECHO: Carrito, Métodos de Pago y Confirmación
        JPanel pnlDerecho = new JPanel(new BorderLayout(8, 8));
        pnlDerecho.setBorder(new TitledBorder("Detalle del Pedido Actual (Carrito)"));

        // Tabla del Carrito
        modeloCarrito = new DefaultTableModel(new String[]{"Ítem / Plato", "Detalles & Extras", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCarrito = new JTable(modeloCarrito);
        tblCarrito.setRowHeight(26);
        pnlDerecho.add(new JScrollPane(tblCarrito), BorderLayout.CENTER);

        // Panel Inferior de Pago y Total
        JPanel pnlPagoTotal = new JPanel(new BorderLayout(10, 10));
        pnlPagoTotal.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Selector de Pago (Adapter)
        JPanel pnlFormaPago = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlFormaPago.setBorder(new TitledBorder("Método de Pago [Adapter]"));
        rbtnYape = new JRadioButton("📱 Yape (BCP)", true);
        rbtnTarjeta = new JRadioButton("💳 Tarjeta Visa/Mastercard", false);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbtnYape);
        bg.add(rbtnTarjeta);
        pnlFormaPago.add(rbtnYape);
        pnlFormaPago.add(rbtnTarjeta);

        JButton btnQuitarItem = new JButton("🗑️ Quitar Ítem");
        btnQuitarItem.addActionListener(e -> accionQuitarItemCarrito());
        pnlFormaPago.add(btnQuitarItem);

        pnlPagoTotal.add(pnlFormaPago, BorderLayout.NORTH);

        // Tarjeta de Totales
        JPanel pnlTotales = new JPanel(new GridLayout(3, 2, 4, 4));
        pnlTotales.setBackground(new Color(241, 245, 249));
        pnlTotales.setBorder(new EmptyBorder(10, 15, 10, 15));

        pnlTotales.add(new JLabel("Subtotal Platos:"));
        lblSubtotal = new JLabel("S/ 0.00", SwingConstants.RIGHT);
        lblSubtotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlTotales.add(lblSubtotal);

        pnlTotales.add(new JLabel("Recargo Canal / Delivery:"));
        lblDescuento = new JLabel("S/ 5.00", SwingConstants.RIGHT);
        lblDescuento.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlTotales.add(lblDescuento);

        JLabel lblTotalEtiqueta = new JLabel("TOTAL A PAGAR:");
        lblTotalEtiqueta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalEtiqueta.setForeground(new Color(15, 23, 42));
        pnlTotales.add(lblTotalEtiqueta);

        lblTotalPagar = new JLabel("S/ 0.00", SwingConstants.RIGHT);
        lblTotalPagar.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalPagar.setForeground(new Color(22, 101, 52)); // Verde dinero
        pnlTotales.add(lblTotalPagar);

        pnlPagoTotal.add(pnlTotales, BorderLayout.CENTER);

        // Botón Principal de Confirmación (Builder + Facade + LocalStorage)
        JButton btnConfirmar = new JButton("✅ CONFIRMAR Y GUARDAR PEDIDO (Guardar en LocalStorage)");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirmar.setBackground(new Color(22, 163, 74));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setPreferredSize(new Dimension(0, 48));
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.addActionListener(e -> accionConfirmarYGuardarPedido());
        pnlPagoTotal.add(btnConfirmar, BorderLayout.SOUTH);

        pnlDerecho.add(pnlPagoTotal, BorderLayout.SOUTH);
        panel.add(pnlDerecho, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // PESTAÑA 2: KDS / COCINA Y GESTIÓN DE PEDIDOS ACTIVOS
    // =========================================================================
    private JPanel crearPestanaKDS() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));

        // Barra de Herramientas y Estado
        JPanel pnlTopKDS = new JPanel(new BorderLayout());
        JLabel lblTituloKDS = new JLabel("📋 PEDIDOS REGISTRADOS EN LOCALSTORAGE (Cola de Atención y KDS)");
        lblTituloKDS.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTopKDS(pnlTopKDS, lblTituloKDS);
        panel.add(pnlTopKDS, BorderLayout.NORTH);

        // Tabla de Pedidos KDS
        modeloPedidosKDS = new DefaultTableModel(new String[]{"ID Pedido", "Cliente", "Canal", "Contenido del Menú", "Total (S/)", "Pago", "Estado Actual"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPedidosKDS = new JTable(modeloPedidosKDS);
        tblPedidosKDS.setRowHeight(30);
        tblPedidosKDS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Colorear columna de estado
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

        // Panel de Control del Ciclo de Vida (STATE + COMMAND + OBSERVER)
        JPanel pnlAccionesKDS = new JPanel(new GridLayout(1, 5, 8, 8));
        pnlAccionesKDS.setBorder(new TitledBorder("Control del Ciclo de Vida del Pedido Seleccionado [State / Command / Observer]"));

        JButton btnAvanzarCocina = new JButton("🍳 1. Pasar a Cocina [Command]");
        btnAvanzarCocina.addActionListener(e -> cambiarEstadoPedidoSeleccionado("EN_COCINA"));

        JButton btnMarcarListo = new JButton("🛎️ 2. Marcar Listo [State]");
        btnMarcarListo.addActionListener(e -> cambiarEstadoPedidoSeleccionado("LISTO"));

        JButton btnEntregar = new JButton("🛵 3. Entregar [State]");
        btnEntregar.addActionListener(e -> cambiarEstadoPedidoSeleccionado("ENTREGADO"));

        JButton btnCancelar = new JButton("❌ Cancelar [Command Undo]");
        btnCancelar.setBackground(new Color(254, 226, 226));
        btnCancelar.addActionListener(e -> cambiarEstadoPedidoSeleccionado("CANCELADO"));

        JButton btnRecargar = new JButton("🔄 Recargar LocalStorage");
        btnRecargar.addActionListener(e -> cargarDatosDesdeLocalStorage());

        pnlAccionesKDS.add(btnAvanzarCocina);
        pnlAccionesKDS.add(btnMarcarListo);
        pnlAccionesKDS.add(btnEntregar);
        pnlAccionesKDS.add(btnCancelar);
        pnlAccionesKDS.add(btnRecargar);

        panel.add(pnlAccionesKDS, BorderLayout.SOUTH);
        return panel;
    }

    private void lblTopKDS(JPanel pnlTopKDS, JLabel lblTituloKDS) {
        pnlTopKDS.add(lblTituloKDS, BorderLayout.WEST);
        lblEstadoKDS = new JLabel("💡 Seleccione un pedido en la tabla para avanzar su estado.");
        lblEstadoKDS.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pnlTopKDS.add(lblEstadoKDS, BorderLayout.EAST);
    }

    // =========================================================================
    // PESTAÑA 3: ALMACÉN E INVENTARIO (PATRÓN PROXY)
    // =========================================================================
    private JPanel crearPestanaAlmacen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));

        // Selector de Roles (Proxy)
        JPanel pnlRol = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlRol.setBorder(new TitledBorder("Control de Acceso y Permisos por Rol [Patrón Proxy]"));
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

        // Tabla de Inventario
        modeloInventario = new DefaultTableModel(new String[]{"Código Insumo", "Descripción", "Stock Disponible", "Unidad", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        modeloInventario.addRow(new Object[]{"CARNE_RES", "Carne Molida Premium 150g", 48, "Porciones", "DISPONIBLE"});
        modeloInventario.addRow(new Object[]{"PAPAS_FRITAS", "Papas Amarillas Pre-cortadas", 95, "Porciones", "DISPONIBLE"});
        modeloInventario.addRow(new Object[]{"GASEOSA_1L", "Bebidas y Refrescos 500ml", 28, "Unidades", "DISPONIBLE"});
        modeloInventario.addRow(new Object[]{"QUESO_CHEDDAR", "Queso Cheddar Tajadas", 40, "Tajadas", "DISPONIBLE"});

        tblInventario = new JTable(modeloInventario);
        tblInventario.setRowHeight(28);
        panel.add(new JScrollPane(tblInventario), BorderLayout.CENTER);

        // Botones de Operación sobre Stock
        JPanel pnlAccionesStock = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnConsultar = new JButton("🔍 Consultar Stock (Permitido a Todos)");
        btnConsultar.addActionListener(e -> {
            int row = tblInventario.getSelectedRow();
            if (row == -1) row = 0;
            String codigo = (String) modeloInventario.getValueAt(row, 0);
            proxyAlmacen.verificarStock(codigo, 1);
            JOptionPane.showMessageDialog(this, "Stock verificado correctamente mediante el PROXY para: " + codigo, "Proxy Consulta OK", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnDescontar = new JButton("📉 Descontar Stock de Insumo (Protegido por Proxy)");
        btnDescontar.addActionListener(e -> {
            int row = tblInventario.getSelectedRow();
            if (row == -1) row = 0;
            String codigo = (String) modeloInventario.getValueAt(row, 0);
            boolean resultado = proxyAlmacen.descontarStock(codigo, 1);
            if (resultado) {
                int stockActual = (int) modeloInventario.getValueAt(row, 2);
                modeloInventario.setValueAt(stockActual - 1, row, 2);
                JOptionPane.showMessageDialog(this, "Stock descontado con éxito por el rol autorizado.", "Proxy OK", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "¡ACCESO DENEGADO POR SEGURIDAD!\nEl rol 'MOZO' no tiene permisos para descontar insumos del almacén.", "Proxy Security Alert", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlAccionesStock.add(btnConsultar);
        pnlAccionesStock.add(btnDescontar);
        panel.add(pnlAccionesStock, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================================================
    // PESTAÑA 4: INSPECTOR DE PATRONES GOF & CONSOLA
    // =========================================================================
    private JPanel crearPestanaAuditoria() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));

        // Matriz de los 15 Patrones con Indicadores de Estado
        JPanel pnlGridPatrones = new JPanel(new GridLayout(3, 5, 6, 6));
        pnlGridPatrones.setBorder(new TitledBorder("Matriz de Validación de los 15 Patrones GOF"));

        String[] nombresPatrones = {
            "1. Singleton (ConexionDB)",
            "2. Abstract Factory (Canales)",
            "3. Factory Method (Pedidos)",
            "4. Prototype (Plantillas)",
            "5. Composite (Combos)",
            "6. Decorator (Extras/Queso)",
            "7. Builder (PedidoBuilder)",
            "8. Proxy (Seguridad Almacén)",
            "9. Memento (Undo/Snapshot)",
            "10. Adapter (Yape/Tarjeta)",
            "11. Bridge (Notificaciones)",
            "12. Command (Órdenes Cocina)",
            "13. State (Ciclo de Vida)",
            "14. Observer (Monitores GPS)",
            "15. Facade (Fachada Maestra)"
        };

        chkPatronesVerificados = new JCheckBox[15];
        for (int i = 0; i < 15; i++) {
            chkPatronesVerificados[i] = new JCheckBox(nombresPatrones[i], true);
            chkPatronesVerificados[i].setEnabled(false);
            chkPatronesVerificados[i].setFont(new Font("Segoe UI", Font.BOLD, 11));
            chkPatronesVerificados[i].setForeground(new Color(22, 101, 52));
            pnlGridPatrones.add(chkPatronesVerificados[i]);
        }
        panel.add(pnlGridPatrones, BorderLayout.NORTH);

        // Consola de Salida de Logs
        JPanel pnlLogs = new JPanel(new BorderLayout(5, 5));
        pnlLogs.setBorder(new TitledBorder("Trazabilidad y Salida de Consola Técnica en Tiempo Real"));

        txtConsolaLogs = new JTextArea();
        txtConsolaLogs.setBackground(new Color(24, 24, 27));
        txtConsolaLogs.setForeground(new Color(74, 222, 128));
        txtConsolaLogs.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtConsolaLogs.setEditable(false);
        txtConsolaLogs.setMargin(new Insets(8, 8, 8, 8));

        pnlLogs.add(new JScrollPane(txtConsolaLogs), BorderLayout.CENTER);

        JPanel pnlBotonesLogs = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton btnEjecutarFacadeTotal = new JButton("⚡ EJECUTAR DEMO INTEGRAL DE 15 PATRONES [FACADE]");
        btnEjecutarFacadeTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEjecutarFacadeTotal.setBackground(new Color(37, 99, 235));
        btnEjecutarFacadeTotal.setForeground(Color.WHITE);
        btnEjecutarFacadeTotal.addActionListener(e -> {
            txtConsolaLogs.setText("");
            facade.procesarFlujoCompletoRestaurante(txtCliente.getText(), (String) cmbCanal.getSelectedItem());
            cargarDatosDesdeLocalStorage();
        });

        JButton btnLimpiarLogs = new JButton("🧹 Limpiar Consola");
        btnLimpiarLogs.addActionListener(e -> txtConsolaLogs.setText(""));

        pnlBotonesLogs.add(btnEjecutarFacadeTotal);
        pnlBotonesLogs.add(btnLimpiarLogs);
        pnlLogs.add(pnlBotonesLogs, BorderLayout.SOUTH);

        panel.add(pnlLogs, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // LÓGICA DE NEGOCIO Y ACCIONES DE EVENTOS
    // =========================================================================

    private void actualizarDetalleCanal() {
        String canal = (String) cmbCanal.getSelectedItem();
        if ("DELIVERY".equalsIgnoreCase(canal)) {
            txtDetalleCanal.setText("Av. Central 456, Lima");
            lblDescuento.setText("S/ 5.00");
        } else if ("SALON".equalsIgnoreCase(canal)) {
            txtDetalleCanal.setText("Mesa #12 (Salón Principal)");
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

        // Aplicar Decoradores (Decorator)
        StringBuilder extrasStr = new StringBuilder();
        if (chkSinCebolla.isSelected()) {
            plato = new SinCebollaDecorator(plato);
            extrasStr.append("Sin Cebolla; ");
        }
        if (chkQueso.isSelected()) {
            plato = new ExtraQuesoDecorator(plato);
            extrasStr.append("Extra Queso Cheddar; ");
        }
        if (chkPapas.isSelected()) {
            plato = new PorcionPapasExtraDecorator(plato);
            extrasStr.append("Porción Papas Extra; ");
        }

        // Aplicar Combo (Composite)
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
            extrasStr.length() > 0 ? extrasStr.toString() : "Estándar",
            String.format("S/ %.2f", itemFinal.getPrecio())
        });

        recalcularTotales();
        System.out.println("[CARRITO] Ítem agregado con éxito: " + itemFinal.getNombre() + " (S/ " + String.format("%.2f", itemFinal.getPrecio()) + ")");
    }

    private void accionQuitarItemCarrito() {
        int row = tblCarrito.getSelectedRow();
        if (row != -1) {
            carritoItems.remove(row);
            modeloCarrito.removeRow(row);
            recalcularTotales();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un ítem del carrito para quitar.", "Aviso", JOptionPane.WARNING_MESSAGE);
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
            System.out.println("✅ [PROTOTYPE] Pedido clonado y cargado al carrito.");
            JOptionPane.showMessageDialog(this, "Plantilla cargada exitosamente mediante el patrón PROTOTYPE.", "Prototype OK", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "El carrito está vacío. Agregue platos antes de confirmar.", "Aviso", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "No hay stock suficiente para procesar el pedido.", "Almacén", JOptionPane.ERROR_MESSAGE);
            return;
        }
        proxyAlmacen.descontarStock("CARNE_RES", 1);

        // 2. Procesar Pago con ADAPTER
        String metodoPagoNombre = rbtnYape.isSelected() ? "Yape (910286670)" : "Tarjeta Visa/MC (**** 5678)";
        IPago pago = rbtnYape.isSelected() ? new YapePaymentAdapter("910286670") : new TarjetaPaymentAdapter("4557880012345678", cliente);
        pago.procesarPago(totalFinal, "ORD-" + System.currentTimeMillis() % 10000);

        // 3. Notificación con BRIDGE
        NotificacionComprobantePago notif = new NotificacionComprobantePago(new EmailImplementador());
        notif.notificar(cliente.toLowerCase().replace(" ", ".") + "@ejemplo.com", "Monto S/ " + String.format("%.2f", totalFinal));

        // 4. Guardar en LocalStorage (Persistencia Simulada)
        String idPedido = "PED-" + (System.currentTimeMillis() % 10000);
        storageDB.guardarPedido(idPedido, cliente, canal, descripcionItems.toString(), totalFinal, metodoPagoNombre, "REGISTRADO");

        // 5. Limpiar Carrito y Notificar
        carritoItems.clear();
        modeloCarrito.setRowCount(0);
        recalcularTotales();
        cargarDatosDesdeLocalStorage();

        JOptionPane.showMessageDialog(this, "¡Pedido #" + idPedido + " registrado y guardado exitosamente en LocalStorage!", "Pedido Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cambiarEstadoPedidoSeleccionado(String nuevoEstado) {
        int row = tblPedidosKDS.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido de la tabla de Cocina/KDS.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idPedido = (String) modeloPedidosKDS.getValueAt(row, 0);
        String estadoAnterior = (String) modeloPedidosKDS.getValueAt(row, 6);

        // Simular Command y State
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

        // Actualizar en LocalStorage
        storageDB.actualizarEstado(idPedido, nuevoEstado);
        cargarDatosDesdeLocalStorage();

        // Notificar con BRIDGE si está listo
        if ("LISTO".equalsIgnoreCase(nuevoEstado)) {
            NotificacionPedidoListo notifListo = new NotificacionPedidoListo(new WhatsAppImplementador());
            notifListo.notificar("+51 910286670", "Tu pedido #" + idPedido + " está listo para despacho.");
        }

        lblEstadoKDS.setText("✅ Pedido #" + idPedido + " cambió de " + estadoAnterior + " ➔ " + nuevoEstado);
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
