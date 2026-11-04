package restaurante.patterns.structural.decorator;

import restaurante.core.interfaces.IComponenteMenu;

/**
 * Decorador concreto para añadir extras (ej: Queso extra, Papas extra, Salsa especial).
 * Responsable: Maria Isabel Ruiz Vasquez
 */
public class AdicionalExtraDecorator extends PlatoDecorator {
    private String nombreExtra;
    private double precioExtra;

    public AdicionalExtraDecorator(IComponenteMenu platoDecorado, String nombreExtra, double precioExtra) {
        super(platoDecorado);
        this.nombreExtra = nombreExtra;
        this.precioExtra = precioExtra;
    }

    @Override
    public String getNombre() {
        return super.getNombre() + " (+ " + nombreExtra + ")";
    }

    @Override
    public double getPrecio() {
        return super.getPrecio() + precioExtra;
    }

    @Override
    public void mostrarDetalle(String indent) {
        super.mostrarDetalle(indent);
        System.out.println(indent + "   [Extra]: " + nombreExtra + " (+ S/ " + String.format("%.2f", precioExtra) + ")");
    }
}
