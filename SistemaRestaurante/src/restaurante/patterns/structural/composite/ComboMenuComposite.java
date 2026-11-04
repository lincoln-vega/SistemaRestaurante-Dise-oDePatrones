package restaurante.patterns.structural.composite;

import java.util.ArrayList;
import java.util.List;
import restaurante.core.interfaces.IComponenteMenu;

/**
 * Patrón Estructural: COMPOSITE
 * Permite tratar tanto a platos individuales como a combos familiares de manera homogénea.
 * Responsable: Maria Isabel Ruiz Vasquez
 */
public class ComboMenuComposite implements IComponenteMenu {
    private String nombreCombo;
    private double descuentoPorcentaje; // ej. 0.10 para 10%
    private List<IComponenteMenu> componentes;

    public ComboMenuComposite(String nombreCombo, double descuentoPorcentaje) {
        this.nombreCombo = nombreCombo;
        this.descuentoPorcentaje = descuentoPorcentaje;
        this.componentes = new ArrayList<>();
    }

    public void agregarComponente(IComponenteMenu componente) {
        componentes.add(componente);
    }

    public void removerComponente(IComponenteMenu componente) {
        componentes.remove(componente);
    }

    @Override
    public String getNombre() {
        return nombreCombo + " [COMBO]";
    }

    @Override
    public double getPrecio() {
        double total = 0;
        for (IComponenteMenu c : componentes) {
            total += c.getPrecio();
        }
        return total * (1.0 - descuentoPorcentaje);
    }

    @Override
    public void mostrarDetalle(String indent) {
        System.out.println(indent + "=== " + getNombre() + " (Total con desc: S/ " + String.format("%.2f", getPrecio()) + ") ===");
        for (IComponenteMenu c : componentes) {
            c.mostrarDetalle(indent + "   ");
        }
    }
}
