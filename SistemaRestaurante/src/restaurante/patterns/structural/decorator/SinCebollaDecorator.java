package restaurante.patterns.structural.decorator;

import restaurante.core.interfaces.IComponenteMenu;

/**
 * Decorador Concreto: Sin Cebolla
 * Modifica la especificacion del plato sin costo adicional (+ S/ 0.00).
 * Responsable: Maria Isabel Ruiz Vasquez
 */
public class SinCebollaDecorator extends PlatoDecorator {

    public SinCebollaDecorator(IComponenteMenu platoDecorado) {
        super(platoDecorado);
    }

    @Override
    public String getNombre() {
        return super.getNombre() + " (Sin Cebolla)";
    }

    @Override
    public double getPrecio() {
        return super.getPrecio();
    }

    @Override
    public void mostrarDetalle(String indent) {
        super.mostrarDetalle(indent);
        System.out.println(indent + "   [DECORATOR]: Indicacion especial (Sin Cebolla) (+ S/ 0.00)");
    }
}
