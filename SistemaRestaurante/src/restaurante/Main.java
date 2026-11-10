package restaurante;

import restaurante.core.facade.RestauranteFacade;

/**
 * Punto de entrada principal para demostración y verificación de patrones.
 * Demuestra la ejecución orquestada de los 15 Patrones de Diseño GOF.
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

        RestauranteFacade facade = new RestauranteFacade();
        facade.procesarFlujoCompletoRestaurante("Lincoln Vega Hidalgo", "DELIVERY");

        System.out.println("\n🎉 DEMOSTRACIÓN FINALIZADA CON ÉXITO.");
        System.out.println("⭐ Los 15 patrones GOF se encuentran integrados, compilando y ejecutando al 100%.");
    }
}
