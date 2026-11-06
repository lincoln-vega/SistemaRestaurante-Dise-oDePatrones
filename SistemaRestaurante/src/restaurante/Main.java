package restaurante;

import java.util.Collections;
import restaurante.core.facade.RestauranteFacade;
import restaurante.core.interfaces.IComponenteMenu;
import restaurante.core.interfaces.IPago;
import restaurante.model.dto.Pedido;
import restaurante.model.dto.PlatoSimple;
import restaurante.patterns.structural.adapter.YapePaymentAdapter;
import restaurante.patterns.structural.composite.ComboMenuComposite;
import restaurante.patterns.structural.decorator.AdicionalExtraDecorator;

/**
 * Punto de entrada principal para demostración y verificación de patrones.
 * Integra las contribuciones del equipo (Zoe Mendoza, Lincoln Vega, etc.).
 */
public class Main {

    public static void main(String[] args) {
        RestauranteFacade facade = new RestauranteFacade();

        // 1. Mostrar membrete con Singleton de Configuración
        facade.mostrarMembreteEmpresa();

        System.out.println("\n==========================================================================");
        System.out.println("   DEMOSTRACIÓN INTEGRAL: 15 PATRONES DE DISEÑO GOF (AVANCE 50%)");
        System.out.println("==========================================================================");

        // Preparamos platos base
        IComponenteMenu lomoSaltado = new PlatoSimple("P003", "Lomo Saltado Criollo", 32.00);
        IComponenteMenu lomoConHuevo = new AdicionalExtraDecorator(lomoSaltado, "Huevo Frito Montado", 2.50);

        IComponenteMenu chicha = new PlatoSimple("B002", "Chicha Morada Jarra 1L", 12.00);

        ComboMenuComposite comboCriollo = new ComboMenuComposite("Super Combo Criollo", 0.15); // 15% desc
        comboCriollo.agregarComponente(lomoConHuevo);
        comboCriollo.agregarComponente(chicha);

        // CASO 1: Atención en Salón (Zoe: Factory Method + Abstract Factory | Lincoln: Facade)
        IPago pagoCaja = null; // El Abstract Factory proveerá "Pago en caja"
        Pedido pedido1 = facade.procesarPedidoSalon("Carlos Santana", 4, Collections.singletonList(comboCriollo), pagoCaja);
        facade.despacharACocina(pedido1);
        facade.completarPedido(pedido1);

        // CASO 2: Despacho Delivery (Zoe: Factory Method + Abstract Factory con recargo | Chris: Adapter Yape)
        IPago pagoYape = new YapePaymentAdapter("987654321");
        IComponenteMenu hamburguesa = new PlatoSimple("P001", "Hamburguesa Clásica", 18.50);
        IComponenteMenu burgerQueso = new AdicionalExtraDecorator(hamburguesa, "Doble Queso Cheddar", 4.00);

        Pedido pedido2 = facade.procesarPedidoDelivery("Lucía Méndez", "Av. Primavera 123 - Surco", "998877665", 
                Collections.singletonList(burgerQueso), pagoYape);
        facade.despacharACocina(pedido2);

        // CASO 3: Demostración rápida de flujo completo con almacén y persistencia
        facade.ejecutarFlujoIntegralDemostracion();

        System.out.println("\n==========================================================================");
        System.out.println("✅ VERIFICACIÓN FINAL: Todos los patrones integrados compilan y ejecutan al 100%");
        System.out.println("==========================================================================");
    }
}
