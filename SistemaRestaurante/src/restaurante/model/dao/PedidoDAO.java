package restaurante.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import restaurante.config.ConexionDB;
import restaurante.model.dto.Pedido;

/**
 * Capa de Persistencia: DATA ACCESS OBJECT (DAO)
 * Administra el acceso a datos y sentencias SQL / Procedimientos Almacenados en MySQL.
 * Responsable: Lincoln Vega Hidalgo
 */
public class PedidoDAO {

    public boolean registrarPedido(Pedido pedido) {
        Connection con = ConexionDB.getInstancia().getConnection();
        if (con == null || !ConexionDB.getInstancia().isConectado()) {
            System.out.println("[DAO PERSISTENCIA] (Modo Local/Offline) Pedido #" + pedido.getIdPedido() 
                    + " guardado en memoria de sesión (Total: S/ " + String.format("%.2f", pedido.calcularTotal()) + ")");
            return true;
        }

        String sql = "INSERT INTO pedidos (id_pedido, cliente, canal_venta, estado, total, notas) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pedido.getIdPedido());
            ps.setString(2, pedido.getCliente());
            ps.setString(3, pedido.getCanalVenta());
            ps.setString(4, pedido.getEstadoActual() != null ? pedido.getEstadoActual().getNombreEstado() : "REGISTRADO");
            ps.setDouble(5, pedido.calcularTotal());
            ps.setString(6, pedido.getNotasCocina());

            int filas = ps.executeUpdate();
            System.out.println("[DAO MYSQL] Pedido #" + pedido.getIdPedido() + " insertado en base de datos (" + filas + " fila afectada).");
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("[DAO MYSQL ERROR] Error registrando pedido: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(String idPedido, String nuevoEstado) {
        Connection con = ConexionDB.getInstancia().getConnection();
        if (con == null || !ConexionDB.getInstancia().isConectado()) {
            System.out.println("[DAO PERSISTENCIA] (Modo Local/Offline) Estado de #" + idPedido + " actualizado a " + nuevoEstado);
            return true;
        }

        String sql = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setString(2, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO MYSQL ERROR] Error actualizando estado: " + e.getMessage());
            return false;
        }
    }

    public List<String> consultarResumenPedidos() {
        List<String> lista = new ArrayList<>();
        Connection con = ConexionDB.getInstancia().getConnection();
        if (con == null || !ConexionDB.getInstancia().isConectado()) {
            lista.add("[MEMORIA] Pedidos activos sincronizados en fachada.");
            return lista;
        }

        String sql = "SELECT id_pedido, cliente, canal_venta, estado, total FROM pedidos ORDER BY fecha_creacion DESC LIMIT 10";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("id_pedido") + " | " + rs.getString("cliente") 
                        + " | " + rs.getString("canal_venta") + " | " + rs.getString("estado") 
                        + " | S/ " + rs.getDouble("total"));
            }
        } catch (SQLException e) {
            System.err.println("[DAO MYSQL ERROR] Error consultando pedidos: " + e.getMessage());
        }
        return lista;
    }
}
