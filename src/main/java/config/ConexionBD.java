package config;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static ConexionBD instancia;
    private Connection conexion;

    private ConexionBD() {
        try {
            Class.forName("org.postgresql.Driver");

            String databaseUrl = System.getenv("DATABASE_URL");
            String dbUrlEnv = System.getenv("DB_URL");

            if (dbUrlEnv != null && !dbUrlEnv.trim().isEmpty()) {
                String user = System.getenv("DB_USER") != null ? System.getenv("DB_USER").trim() : "neondb_owner";
                String pass = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD").trim() : "npg_kWnBAFU3V6Dw";
                this.conexion = DriverManager.getConnection(dbUrlEnv.trim(), user, pass);
                System.out.println("Conexión exitosa a PostgreSQL usando DB_URL.");
            } else if (databaseUrl != null && !databaseUrl.trim().isEmpty()) {
                URI uri = new URI(databaseUrl.trim());
                String[] userInfo = uri.getUserInfo().split(":", 2);
                String username = userInfo[0];
                String password = userInfo.length > 1 ? userInfo[1] : "";
                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String query = uri.getQuery() != null ? "?" + uri.getQuery() : "?sslmode=require";

                String dbUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath() + query;
                this.conexion = DriverManager.getConnection(dbUrl, username, password);
                System.out.println("Conexión exitosa a PostgreSQL usando DATABASE_URL.");
            } else {
                String jdbcUrl = "jdbc:postgresql://ep-small-violet-be39spac-pooler.c-9.us-east-2.aws.neon.tech:5432/neondb?sslmode=require";
                this.conexion = DriverManager.getConnection(jdbcUrl, "neondb_owner", "npg_kWnBAFU3V6Dw");
                System.out.println("Conexión establecida usando respaldo directo a Neon.");
            }
        } catch (Exception e) {
            System.err.println("Error de conexión BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized ConexionBD getInstance() {
        try {
            if (instancia == null || instancia.getConexion() == null || instancia.getConexion().isClosed() || !instancia.getConexion().isValid(2)) {
                instancia = new ConexionBD();
            }
        } catch (SQLException e) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }
}