package dev.franek.customitems.domain.impl.backpack.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import dev.franek.customitems.database.Database;
import dev.franek.customitems.database.DatabaseQuery;
import dev.franek.customitems.database.DatabaseQueue;
import dev.franek.customitems.database.query.QueryBuilder;
import dev.franek.customitems.domain.impl.backpack.data.BackpackData;
import dev.franek.customitems.domain.impl.backpack.data.BackpackDataStorage;
import dev.franek.customitems.domain.impl.purse.PurseItemSerializer;
import dev.franek.customitems.shared.logger.FastLogger;

public class BackpackDataRepository {
    private final String tableName = "franek_customitems_backpacks";
    private final Database database;
    private final DatabaseQueue databaseQueue;
    private final BackpackDataStorage backpackDataStorage;

    public BackpackDataRepository(Database database, DatabaseQueue databaseQueue, BackpackDataStorage backpackDataStorage) {
        this.database = database;
        this.databaseQueue = databaseQueue;
        this.backpackDataStorage = backpackDataStorage;
        this.initialize();
        this.load();
    }

    public void initialize() {
        try (Connection connection = this.database.getConnection();){
            if (connection == null) {
                FastLogger.error("[BackpackDataRepository] Brak połączenia z bazą danych!");
                return;
            }
            QueryBuilder.createTable(this.tableName, this.database.getDatabaseType()).column("uniqueId", String.class, true).rawColumn("`content` MEDIUMTEXT").build().executeUpdate(connection);
        }
        catch (SQLException exception) {
            FastLogger.error("[BackpackDataRepository] Wystąpił błąd podczas inicjalizacji!");
            FastLogger.error("[BackpackDataRepository] Wiadomość: " + exception.getMessage());
        }
    }

    public void load() {
        try (Connection connection = this.database.getConnection();){
            if (connection == null) {
                FastLogger.error("[BackpackDataRepository] Brak połączenia z bazą danych!");
                return;
            }
            QueryBuilder.select(this.tableName).build().executeQuery(connection, resultSet -> {
                while (resultSet.next()) {
                    BackpackData backpackData = new BackpackData(UUID.fromString(resultSet.getString("uniqueId")), PurseItemSerializer.deserialize(resultSet.getBytes("content")));
                    this.backpackDataStorage.add(backpackData);
                }
            });
        }
        catch (SQLException exception) {
            FastLogger.error("[BackpackDataRepository] Wystąpił błąd podczas ładowania danych!");
            FastLogger.error("[BackpackDataRepository] Wiadomość: " + exception.getMessage());
        }
    }

    public void save(BackpackData backpackData, boolean priority) {
        if (priority) {
            this.databaseQueue.enqueuePriority(this.buildUpsert(backpackData));
        } else {
            this.databaseQueue.enqueue(this.buildUpsert(backpackData));
        }
    }

    public void delete(UUID backpackUniqueId, boolean priority) {
        DatabaseQuery query = QueryBuilder.delete(this.tableName).where("uniqueId", backpackUniqueId.toString()).build();
        if (priority) {
            this.databaseQueue.enqueuePriority(query);
        } else {
            this.databaseQueue.enqueue(query);
        }
    }

    private DatabaseQuery buildUpsert(BackpackData data) {
        return QueryBuilder.upsert(this.tableName, this.database.getDatabaseType()).set("uniqueId", data.getUniqueId().toString()).set("content", PurseItemSerializer.serialize(data.getContent())).build();
    }
}

