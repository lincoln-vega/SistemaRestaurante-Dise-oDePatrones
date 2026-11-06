package restaurante.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Patrón Creacional: SINGLETON
 * Administra de forma única y centralizada la conexión JDBC hacia MySQL.
 * Responsable: Lincoln Vega Hidalgo
 */
public class ConexionDB {
    private static volatile ConexionDB instancia;
    private Connection conexion;
    private boolean modoOffline = false;

    private static final String URL = "jdbc:mysql://localhost:3306/db_restaurante?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    private ConexionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("[SINGLETON] Conexión MySQL inicializada con éxito.");
        } catch (ClassNotFoundException e) {
            System.err.println("[SINGLETON ERROR] Driver MySQL no encontrado: " + e.getMessage());
            this.modoOffline = true;
        } catch (SQLException e) {
            System.out.println("[SINGLETON] Modo local/offline activo (MySQL no detectado en localhost).");
            this.modoOffline = true;
        }
    }

    public static ConexionDB getInstancia() {
        if (instancia == null) {
            synchronized (ConexionDB.class) {
                if (instancia == null) {
                    instancia = new ConexionDB();
                }
            }
        }
        return instancia;
    }

    public boolean isConectado() {
        try {
            return this.conexion != null && !this.conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public Connection getConnection() {
        if (modoOffline) {
            return null;
        }
        try {
            if (this.conexion == null || this.conexion.isClosed()) {
                this.conexion = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (SQLException e) {
            modoOffline = true;
        }
        return this.conexion;
    }
}
