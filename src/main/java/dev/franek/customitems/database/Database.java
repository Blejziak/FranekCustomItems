package dev.franek.customitems.database;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import dev.franek.customitems.database.DatabaseType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.franek.customitems.shared.logger.FastLogger;

public class Database {
    private HikariDataSource dataSource;
    private DatabaseType databaseType;

    public void connectMySQL(String host, int port, String base, String user, String password) {
        this.databaseType = DatabaseType.MYSQL;
        if (user.isEmpty() || password.isEmpty()) {
            FastLogger.error(user.isEmpty() ? "[Database] Nazwa użytkownika nie może być pusta!" : "[Database] Hasło nie może być puste!");
            return;
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + base + "?useSSL=false&characterEncoding=UTF-8&serverTimezone=Europe/Warsaw");
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(10000L);
        config.setIdleTimeout(600000L);
        config.setMaxLifetime(1800000L);
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        this.dataSource = new HikariDataSource(config);
    }

    public void connectSQLite(File dataFolder) {
        this.databaseType = DatabaseType.SQLITE;
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        File databaseFile = new File(dataFolder, "database.db");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000L);
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("journal_mode", "DELETE");
        config.addDataSourceProperty("synchronous", "NORMAL");
        config.addDataSourceProperty("foreign_keys", "true");
        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        }
        catch (SQLException exception) {
            FastLogger.error("[Database] Błąd połączenia: " + exception.getMessage());
            return null;
        }
    }

    public boolean isConnected() {
        if (this.databaseType == DatabaseType.SQLITE) {
            return true;
        }
        try (Connection connection = this.dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }

    public void disconnect() {
        if (this.dataSource != null && !this.dataSource.isClosed()) {
            this.dataSource.close();
        }
    }
    public DatabaseType getDatabaseType() {
        return this.databaseType;
    }
}

