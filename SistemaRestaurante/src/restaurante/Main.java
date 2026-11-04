package restaurante;

import restaurante.core.facade.RestauranteFacade;

/**
 * Punto de entrada principal para demostración y verificación de patrones.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("   SISTEMA DE GESTIÓN DE PEDIDOS - CADENA DE RESTAURANTES");
        System.out.println("   Avance 50% - Patrones de Diseño (Semanas 1 a 13)");
        System.out.println("==========================================================\n");

        RestauranteFacade facade = new RestauranteFacade();
        facade.procesarPedidoEjemplo("Lincoln Vega", "SALON");

        System.out.println("\n🎉 Todos los módulos y contratos de patrones han sido integrados con éxito.");
    }
}
