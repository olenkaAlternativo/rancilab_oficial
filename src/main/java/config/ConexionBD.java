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

            if (databaseUrl != null && !databaseUrl.isEmpty()) {
                URI uri = new URI(databaseUrl);
                String[] userInfo = uri.getUserInfo().split(":", 2);
                String username = userInfo[0];
                String password = userInfo.length > 1 ? userInfo[1] : "";
                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String query = uri.getQuery() != null ? "?" + uri.getQuery() : "?sslmode=require";

                String dbUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath() + query;

                this.conexion = DriverManager.getConnection(dbUrl, username, password);
                System.out.println("Conexión exitosa a PostgreSQL usando DATABASE_URL.");
            } else {
                String host = System.getenv("PGHOST") != null ? System.getenv("PGHOST") : "localhost";
                String port = System.getenv("PGPORT") != null ? System.getenv("PGPORT") : "5432";
                String db   = System.getenv("PGDATABASE") != null ? System.getenv("PGDATABASE") : "neondb";
                String user = System.getenv("PGUSER") != null ? System.getenv("PGUSER") : "neondb_owner";
                String pass = System.getenv("PGPASSWORD") != null ? System.getenv("PGPASSWORD") : "";
                String ssl  = System.getenv("PGSSLMODE") != null ? System.getenv("PGSSLMODE") : "require";

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db + "?sslmode=" + ssl;
                this.conexion = DriverManager.getConnection(jdbcUrl, user, pass);
                System.out.println("Conexión establecida usando variables individuales.");
            }
        } catch (Exception e) {
            System.err.println("Error de conexión BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized ConexionBD getInstance() {
        try {
            if (instancia == null || instancia.getConexion() == null || instancia.getConexion().isClosed()) {
                instancia = new ConexionBD();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }
}