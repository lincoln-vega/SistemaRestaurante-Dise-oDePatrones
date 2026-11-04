package restaurante.patterns.behavioral.memento;

import java.util.Stack;

/**
 * Caretaker para el patrón Memento.
 * Gestiona la pila de instantáneas para operaciones de deshacer (Undo).
 * Responsable: Jairo Uriel Mejia Huerta
 */
public class HistorialPedidoCaretaker {
    private Stack<PedidoMemento> historial = new Stack<>();

    public void guardarEstado(PedidoMemento memento) {
        historial.push(memento);
        System.out.println("[MEMENTO] Instantánea guardada (Puntos de restauración: " + historial.size() + ")");
    }

    public PedidoMemento deshacer() {
        if (!historial.isEmpty()) {
            return historial.pop();
        }
        return null;
    }

    public boolean puedeDeshacer() {
        return !historial.isEmpty();
    }
}
