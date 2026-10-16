package dev.franek.customitems.domain.impl.purse.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import dev.franek.customitems.database.Database;
import dev.franek.customitems.database.DatabaseQuery;
import dev.franek.customitems.database.DatabaseQueue;
import dev.franek.customitems.database.query.QueryBuilder;
import dev.franek.customitems.domain.impl.purse.PurseItemSerializer;
import dev.franek.customitems.domain.impl.purse.data.PurseData;
import dev.franek.customitems.domain.impl.purse.data.PurseDataStorage;
import dev.franek.customitems.shared.logger.FastLogger;

public class PurseDataRepository {
    private final String tableName = "franek_customitems_purses";
    private final Database database;
    private final DatabaseQueue databaseQueue;
    private final PurseDataStorage purseDataStorage;

    public PurseDataRepository(Database database, DatabaseQueue databaseQueue, PurseDataStorage purseDataStorage) {
        this.database = database;
        this.databaseQueue = databaseQueue;
        this.purseDataStorage = purseDataStorage;
        this.initialize();
        this.load();
    }

    public void initialize() {
        try (Connection connection = this.database.getConnection();){
            if (connection == null) {
                FastLogger.error("[PurseDataRepository] Brak połączenia z bazą danych!");
                return;
            }
            QueryBuilder.createTable(this.tableName, this.database.getDatabaseType()).column("uniqueId", String.class, true).rawColumn("`content` MEDIUMTEXT").build().executeUpdate(connection);
        }
        catch (SQLException exception) {
            FastLogger.error("[PurseDataRepository] Wystąpił błąd podczas inicjalizacji!");
            FastLogger.error("[PurseDataRepository] Wiadomość: " + exception.getMessage());
        }
    }

    public void load() {
        try (Connection connection = this.database.getConnection();){
            if (connection == null) {
                FastLogger.error("[PurseDataRepository] Brak połączenia z bazą danych!");
                return;
            }
            QueryBuilder.select(this.tableName).build().executeQuery(connection, resultSet -> {
                while (resultSet.next()) {
                    PurseData purseData = new PurseData(UUID.fromString(resultSet.getString("uniqueId")), PurseItemSerializer.deserialize(resultSet.getBytes("content")));
                    this.purseDataStorage.add(purseData);
                }
            });
        }
        catch (SQLException exception) {
            FastLogger.error("[PurseDataRepository] Wystąpił błąd podczas ładowania danych!");
            FastLogger.error("[PurseDataRepository] Wiadomość: " + exception.getMessage());
        }
    }

    public void save(PurseData purseData, boolean priority) {
        if (priority) {
            this.databaseQueue.enqueuePriority(this.buildUpsert(purseData));
        } else {
            this.databaseQueue.enqueue(this.buildUpsert(purseData));
        }
    }

    public void delete(UUID purseUniqueId, boolean priority) {
        DatabaseQuery query = QueryBuilder.delete(this.tableName).where("uniqueId", purseUniqueId.toString()).build();
        if (priority) {
            this.databaseQueue.enqueuePriority(query);
        } else {
            this.databaseQueue.enqueue(query);
        }
    }

    private DatabaseQuery buildUpsert(PurseData data) {
        return QueryBuilder.upsert(this.tableName, this.database.getDatabaseType()).set("uniqueId", data.getUniqueId().toString()).set("content", PurseItemSerializer.serialize(data.getContent())).build();
    }
}

