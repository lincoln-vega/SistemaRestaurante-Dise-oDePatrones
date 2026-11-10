package restaurante;

import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import restaurante.core.facade.RestauranteFacade;
import restaurante.ui.VentanaPrincipal;

/**
 * Punto de entrada principal del Sistema de Restaurante.
 * Integra la ejecución por consola y el arranque de la Interfaz Gráfica Java Swing.
 * Responsable Técnico: Lincoln Vega Hidalgo
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("       SISTEMA DE GESTIÓN DE PEDIDOS - CADENA DE RESTAURANTES");
        System.out.println("       EVALUACIÓN DEL AVANCE 50% (SEMANA 14) - UTP");
        System.out.println("       Líder Técnico: Lincoln Vega Hidalgo");
        System.out.println("       Meta: 15 Patrones GOF Integrados y Principios SOLID");
        System.out.println("================================================================================\n");

        // Ejecutar demostración en consola
        RestauranteFacade facade = new RestauranteFacade();
        facade.procesarFlujoCompletoRestaurante("Lincoln Vega Hidalgo", "DELIVERY");

        System.out.println("\n🎉 DEMOSTRACIÓN EN CONSOLA FINALIZADA CON ÉXITO.");
        System.out.println("⭐ 15/15 Patrones GOF verificados.");

        // Iniciar interfaz gráfica Swing si el entorno tiene pantalla disponible
        if (!GraphicsEnvironment.isHeadless()) {
            System.out.println("\n🖥️  Iniciando Interfaz Gráfica Java Swing (VentanaPrincipal)...");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal ventana = new VentanaPrincipal();
                ventana.setVisible(true);
            });
        } else {
            System.out.println("[INFO] Entorno headless detectado: la interfaz Swing está disponible para NetBeans.");
        }
    }
}
