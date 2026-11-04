package restaurante.model.dto;

import restaurante.core.interfaces.IComponenteMenu;

/**
 * Representa un plato individual o ítem básico de la carta.
 * Implementa IComponenteMenu para servir de hoja en Composite o base en Decorator.
 */
public class PlatoSimple implements IComponenteMenu {
    private String id;
    private String nombre;
    private double precio;

    public PlatoSimple(String id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public double getPrecio() {
        return precio;
    }

    @Override
    public void mostrarDetalle(String indent) {
        System.out.println(indent + "- " + nombre + " : S/ " + String.format("%.2f", precio));
    }
}
