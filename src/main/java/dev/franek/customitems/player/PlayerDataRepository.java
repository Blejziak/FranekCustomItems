package dev.franek.customitems.player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import dev.franek.customitems.database.Database;
import dev.franek.customitems.database.DatabaseQuery;
import dev.franek.customitems.database.DatabaseQueue;
import dev.franek.customitems.database.query.QueryBuilder;
import dev.franek.customitems.player.PlayerData;
import dev.franek.customitems.player.PlayerDataStorage;
import dev.franek.customitems.shared.logger.FastLogger;

public class PlayerDataRepository {
    private final String tableName = "franek_customitems_players";
    private final Database database;
    private final DatabaseQueue databaseQueue;
    private final PlayerDataStorage playerDataStorage;

    public PlayerDataRepository(Database database, DatabaseQueue databaseQueue, PlayerDataStorage playerDataStorage) {
        this.database = database;
        this.databaseQueue = databaseQueue;
        this.playerDataStorage = playerDataStorage;
        this.initialize();
        this.load();
    }

    public void initialize() {
        try (Connection connection = this.database.getConnection();){
            if (connection == null) {
                FastLogger.error("[PlayerDataRepository] Brak połączenia z bazą danych!");
                return;
            }
            QueryBuilder.createTable(this.tableName, this.database.getDatabaseType()).column("uniqueId", String.class, true).column("name", String.class).build().executeUpdate(connection);
        }
        catch (SQLException exception) {
            FastLogger.error("[PlayerDataRepository] Wystąpił błąd podczas inicjalizacji!");
            FastLogger.error("[PlayerDataRepository] Wiadomość: " + exception.getMessage());
        }
    }

    public void load() {
        try (Connection connection = this.database.getConnection();){
            if (connection == null) {
                FastLogger.error("[PlayerDataRepository] Brak połączenia z bazą danych!");
                return;
            }
            QueryBuilder.select(this.tableName).build().executeQuery(connection, resultSet -> {
                while (resultSet.next()) {
                    PlayerData playerData = new PlayerData(UUID.fromString(resultSet.getString("uniqueId")), resultSet.getString("name"));
                    this.playerDataStorage.add(playerData);
                }
            });
        }
        catch (SQLException exception) {
            FastLogger.error("[PlayerDataRepository] Wystąpił błąd podczas ładowania danych!");
            FastLogger.error("[PlayerDataRepository] Wiadomość: " + exception.getMessage());
        }
    }

    public void save(PlayerData playerData, boolean priority) {
        if (priority) {
            this.databaseQueue.enqueuePriority(this.buildUpsert(playerData));
        } else {
            this.databaseQueue.enqueue(this.buildUpsert(playerData));
        }
    }

    private DatabaseQuery buildUpsert(PlayerData data) {
        return QueryBuilder.upsert(this.tableName, this.database.getDatabaseType()).set("uniqueId", data.getUniqueId().toString()).set("name", data.getName()).build();
    }
}

