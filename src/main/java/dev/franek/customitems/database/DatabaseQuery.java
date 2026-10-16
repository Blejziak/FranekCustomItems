package dev.franek.customitems.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

public class DatabaseQuery {
    private final String sql;
    private final List<Object> params;
    private final Instant timeOfCreation;

    public DatabaseQuery(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
        this.timeOfCreation = Instant.now();
    }

    public void executeUpdate(Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(this.sql);){
            this.bindParams(stmt);
            stmt.executeUpdate();
        }
    }

    public void executeQuery(Connection connection, ResultSetConsumer consumer) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(this.sql);){
            this.bindParams(stmt);
            try (ResultSet resultSet = stmt.executeQuery();){
                consumer.accept(resultSet);
            }
        }
    }

    private void bindParams(PreparedStatement stmt) throws SQLException {
        for (int i = 0; i < this.params.size(); ++i) {
            stmt.setObject(i + 1, this.params.get(i));
        }
    }
    public String getSql() {
        return this.sql;
    }
    public List<Object> getParams() {
        return this.params;
    }
    public Instant getTimeOfCreation() {
        return this.timeOfCreation;
    }

    @FunctionalInterface
    public static interface ResultSetConsumer {
        public void accept(ResultSet var1) throws SQLException;
    }
}

