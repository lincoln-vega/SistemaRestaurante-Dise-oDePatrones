package restaurante.core.interfaces;

/**
 * Contrato base para elementos del menú (Platos individuales, Combos, y Adicionales).
 * Utilizado por los patrones Composite y Decorator.
 * Responsable: Maria Isabel Ruiz Vasquez
 */
public interface IComponenteMenu {
    String getNombre();
    double getPrecio();
    void mostrarDetalle(String indent);
}
