package restaurante.patterns.structural.decorator;

import restaurante.core.interfaces.IComponenteMenu;

/**
 * Decorador Concreto: Porción de Papas Extra
 * Agrega una porción adicional de papas fritas aumentando su costo en S/ 4.50.
 * Responsable: Maria Isabel Ruiz Vasquez
 */
public class PorcionPapasExtraDecorator extends PlatoDecorator {
    private static final double PRECIO_EXTRA = 4.50;

    public PorcionPapasExtraDecorator(IComponenteMenu platoDecorado) {
        super(platoDecorado);
    }

    @Override
    public String getNombre() {
        return super.getNombre() + " [Porción Papas Extra]";
    }

    @Override
    public double getPrecio() {
        return super.getPrecio() + PRECIO_EXTRA;
    }

    @Override
    public void mostrarDetalle(String indent) {
        super.mostrarDetalle(indent);
        System.out.println(indent + "   🍟 [DECORATOR]: Porción Papas Extra (+ S/ " + String.format("%.2f", PRECIO_EXTRA) + ")");
    }
}
