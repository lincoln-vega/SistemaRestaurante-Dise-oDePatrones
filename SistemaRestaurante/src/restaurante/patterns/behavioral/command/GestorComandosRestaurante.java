package restaurante.patterns.behavioral.command;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import restaurante.core.interfaces.IComando;

/**
 * Invocador del Patrón COMMAND (Gestor de Comandos / Invoker)
 * Mantiene una cola de comandos pendientes por ejecutar y una pila de historial para operaciones 'Deshacer' (Undo).
 * Responsable: Etxin Sabiniano Malvas Basilio
 */
public class GestorComandosRestaurante {
    private Queue<IComando> colaComandos = new LinkedList<>();
    private Stack<IComando> historialEjecutados = new Stack<>();

    public void encolarComando(IComando comando) {
        colaComandos.offer(comando);
        System.out.println("[GESTOR COMANDOS] Encolado: " + comando.getDescripcion());
    }

    public void procesarCola() {
        System.out.println("\n--- Procesando Cola de Comandos de Operación ---");
        while (!colaComandos.isEmpty()) {
            IComando cmd = colaComandos.poll();
            cmd.ejecutar();
            historialEjecutados.push(cmd);
        }
    }

    public void ejecutarComandoDirecto(IComando comando) {
        System.out.println("\n--- Ejecución Inmediata de Comando ---");
        comando.ejecutar();
        historialEjecutados.push(comando);
    }

    public void deshacerUltimoComando() {
        if (!historialEjecutados.isEmpty()) {
            IComando cmd = historialEjecutados.pop();
            System.out.println("\n--- Deshaciendo Operación ---");
            System.out.println("[GESTOR COMANDOS] Deshaciendo: " + cmd.getDescripcion());
            cmd.deshacer();
        } else {
            System.out.println("[GESTOR COMANDOS] No hay comandos previos para deshacer.");
        }
    }
}
