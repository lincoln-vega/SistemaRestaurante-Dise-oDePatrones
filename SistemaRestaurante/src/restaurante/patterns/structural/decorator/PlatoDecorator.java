package restaurante.patterns.structural.decorator;

import restaurante.core.interfaces.IComponenteMenu;

/**
 * Patrón Estructural: DECORATOR (Decorador Abstracto)
 * Permite agregar dinámicamente adicionales, salsas o modificaciones a un plato base.
 * Responsable: Maria Isabel Ruiz Vasquez
 */
public abstract class PlatoDecorator implements IComponenteMenu {
    protected IComponenteMenu platoDecorado;

    public PlatoDecorator(IComponenteMenu platoDecorado) {
        this.platoDecorado = platoDecorado;
    }

    @Override
    public String getNombre() {
        return platoDecorado.getNombre();
    }

    @Override
    public double getPrecio() {
        return platoDecorado.getPrecio();
    }

    @Override
    public void mostrarDetalle(String indent) {
        platoDecorado.mostrarDetalle(indent);
    }
}
