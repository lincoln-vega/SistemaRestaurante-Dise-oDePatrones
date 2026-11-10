package restaurante.patterns.structural.decorator;

import restaurante.core.interfaces.IComponenteMenu;

/**
 * Decorador Concreto: Extra Queso
 * Agrega queso adicional al plato aumentando su costo en S/ 3.00.
 * Responsable: Maria Isabel Ruiz Vasquez
 */
public class ExtraQuesoDecorator extends PlatoDecorator {
    private static final double PRECIO_EXTRA = 3.00;

    public ExtraQuesoDecorator(IComponenteMenu platoDecorado) {
        super(platoDecorado);
    }

    @Override
    public String getNombre() {
        return super.getNombre() + " [Extra Queso Cheddar]";
    }

    @Override
    public double getPrecio() {
        return super.getPrecio() + PRECIO_EXTRA;
    }

    @Override
    public void mostrarDetalle(String indent) {
        super.mostrarDetalle(indent);
        System.out.println(indent + "   [DECORATOR]: Extra Queso Cheddar (+ S/ " + String.format("%.2f", PRECIO_EXTRA) + ")");
    }
}
