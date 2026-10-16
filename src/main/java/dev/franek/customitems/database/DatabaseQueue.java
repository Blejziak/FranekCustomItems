package dev.franek.customitems.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import dev.franek.customitems.database.Database;
import dev.franek.customitems.database.DatabaseQuery;
import dev.franek.customitems.shared.logger.FastLogger;

public class DatabaseQueue {
    private final Database database;
    private final Queue<DatabaseQuery> normalQueue = new ConcurrentLinkedQueue<DatabaseQuery>();
    private final Queue<DatabaseQuery> priorityQueue = new ConcurrentLinkedQueue<DatabaseQuery>();

    public void enqueue(DatabaseQuery query) {
        this.normalQueue.add(query);
    }

    public void enqueuePriority(DatabaseQuery query) {
        this.priorityQueue.add(query);
    }

    public void flush() {
        if (this.normalQueue.isEmpty() && this.priorityQueue.isEmpty()) {
            return;
        }
        try (Connection connection = this.database.getConnection();){
            if (connection == null) {
                return;
            }
            this.processQueue(connection, this.priorityQueue);
            this.processQueue(connection, this.normalQueue);
        }
        catch (SQLException exception) {
            FastLogger.error("[DatabaseActionQueue] Wystąpił błąd podczas flushowania kolejki!");
            FastLogger.error("[DatabaseActionQueue] Wiadomość: " + exception.getMessage());
        }
    }

    private void processQueue(Connection connection, Queue<DatabaseQuery> queue) {
        while (!queue.isEmpty()) {
            DatabaseQuery query = queue.poll();
            if (query == null) continue;
            try {
                query.executeUpdate(connection);
            }
            catch (SQLException e) {
                FastLogger.error("[DatabaseActionQueue] Błąd przy wykonaniu zapytania: " + e.getMessage());
                FastLogger.error("[DatabaseActionQueue] SQL: " + query.getSql());
            }
        }
    }
    public DatabaseQueue(Database database) {
        this.database = database;
    }
}

