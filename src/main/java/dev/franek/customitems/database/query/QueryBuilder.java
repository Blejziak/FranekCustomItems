package dev.franek.customitems.database.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import dev.franek.customitems.database.DatabaseQuery;
import dev.franek.customitems.database.DatabaseType;

public class QueryBuilder {
    public static CreateTable createTable(String tableName, DatabaseType databaseType) {
        return new CreateTable(tableName, databaseType);
    }

    public static Upsert upsert(String tableName, DatabaseType databaseType) {
        return new Upsert(tableName, databaseType);
    }

    public static Update update(String tableName) {
        return new Update(tableName);
    }

    public static Select select(String tableName) {
        return new Select(tableName);
    }

    public static Delete delete(String tableName) {
        return new Delete(tableName);
    }

    public static class CreateTable {
        private final String tableName;
        private final DatabaseType databaseType;
        private final List<String> columns = new ArrayList<String>();

        public CreateTable rawColumn(String definition) {
            this.columns.add(definition);
            return this;
        }

        public CreateTable column(String name, Class<?> type) {
            return this.column(name, type, false);
        }

        public CreateTable column(String name, Class<?> type, boolean primaryKey) {
            String sqlType = this.toSqlType(type);
            String definition = "`" + name + "` " + sqlType;
            if (primaryKey) {
                definition = definition + " PRIMARY KEY";
            }
            this.columns.add(definition);
            return this;
        }

        public DatabaseQuery build() {
            String sql = this.databaseType == DatabaseType.SQLITE ? "CREATE TABLE IF NOT EXISTS `" + this.tableName + "` (" + String.join((CharSequence)", ", this.columns) + ")" : "CREATE TABLE IF NOT EXISTS `" + this.tableName + "` (" + String.join((CharSequence)", ", this.columns) + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            return new DatabaseQuery(sql, new ArrayList<Object>());
        }

        private String toSqlType(Class<?> type) {
            if (type == String.class) {
                return "VARCHAR(255) NOT NULL";
            }
            if (type == Integer.class || type == Integer.TYPE) {
                return "INT NOT NULL DEFAULT 0";
            }
            if (type == Long.class || type == Long.TYPE) {
                return "BIGINT NOT NULL DEFAULT 0";
            }
            if (type == Double.class || type == Double.TYPE) {
                return "DOUBLE NOT NULL DEFAULT 0";
            }
            if (type == Boolean.class || type == Boolean.TYPE) {
                return "TINYINT(1) NOT NULL DEFAULT 0";
            }
            if (type == Float.class || type == Float.TYPE) {
                return "FLOAT NOT NULL DEFAULT 0";
            }
            throw new IllegalArgumentException("Nieobsługiwany typ kolumny: " + type.getSimpleName());
        }
        public CreateTable(String tableName, DatabaseType databaseType) {
            this.tableName = tableName;
            this.databaseType = databaseType;
        }
    }

    public static class Upsert {
        private final String tableName;
        private final DatabaseType databaseType;
        private final Map<String, Object> data = new LinkedHashMap<String, Object>();

        private Upsert(String tableName, DatabaseType databaseType) {
            this.tableName = tableName;
            this.databaseType = databaseType;
        }

        public Upsert set(String column, Object value) {
            this.data.put(column, value);
            return this;
        }

        public DatabaseQuery build() {
            String sql;
            ArrayList<String> columns = new ArrayList<String>(this.data.keySet());
            ArrayList<Object> params = new ArrayList<Object>(this.data.values());
            ArrayList<String> placeholders = new ArrayList<String>();
            for (int i = 0; i < columns.size(); ++i) {
                placeholders.add("?");
            }
            if (this.databaseType == DatabaseType.SQLITE) {
                sql = "INSERT OR REPLACE INTO `" + this.tableName + "` (`" + String.join((CharSequence)"`, `", columns) + "`) VALUES (" + String.join((CharSequence)", ", placeholders) + ")";
            } else {
                ArrayList<String> updates = new ArrayList<String>();
                for (String itColumn : columns) {
                    updates.add("`" + itColumn + "` = VALUES(`" + itColumn + "`)");
                }
                sql = "INSERT INTO `" + this.tableName + "` (`" + String.join((CharSequence)"`, `", columns) + "`) VALUES (" + String.join((CharSequence)", ", placeholders) + ") ON DUPLICATE KEY UPDATE " + String.join((CharSequence)", ", updates);
            }
            return new DatabaseQuery(sql, params);
        }
    }

    public static class Update {
        private final String tableName;
        private final Map<String, Object> data = new LinkedHashMap<String, Object>();
        private String whereColumn;
        private Object whereValue;

        private Update(String tableName) {
            this.tableName = tableName;
        }

        public Update set(String column, Object value) {
            this.data.put(column, value);
            return this;
        }

        public Update where(String column, Object value) {
            this.whereColumn = column;
            this.whereValue = value;
            return this;
        }

        public DatabaseQuery build() {
            if (this.whereColumn == null) {
                throw new IllegalStateException("UPDATE wymaga .where()");
            }
            ArrayList<String> setClauses = new ArrayList<String>();
            ArrayList<Object> params = new ArrayList<Object>();
            for (Map.Entry<String, Object> entry : this.data.entrySet()) {
                setClauses.add("`" + entry.getKey() + "` = ?");
                params.add(entry.getValue());
            }
            params.add(this.whereValue);
            String sql = "UPDATE `" + this.tableName + "` SET " + String.join((CharSequence)", ", setClauses) + " WHERE `" + this.whereColumn + "` = ?";
            return new DatabaseQuery(sql, params);
        }
    }

    public static class Select {
        private final String tableName;
        private String whereColumn;
        private Object whereValue;

        private Select(String tableName) {
            this.tableName = tableName;
        }

        public Select where(String column, Object value) {
            this.whereColumn = column;
            this.whereValue = value;
            return this;
        }

        public DatabaseQuery build() {
            if (this.whereColumn == null) {
                return new DatabaseQuery("SELECT * FROM `" + this.tableName + "`", new ArrayList<Object>());
            }
            ArrayList<Object> params = new ArrayList<Object>();
            params.add(this.whereValue);
            String sql = "SELECT * FROM `" + this.tableName + "` WHERE `" + this.whereColumn + "` = ?";
            return new DatabaseQuery(sql, params);
        }
    }

    public static class Delete {
        private final String tableName;
        private String whereColumn;
        private Object whereValue;

        private Delete(String tableName) {
            this.tableName = tableName;
        }

        public Delete where(String column, Object value) {
            this.whereColumn = column;
            this.whereValue = value;
            return this;
        }

        public DatabaseQuery build() {
            if (this.whereColumn == null) {
                throw new IllegalStateException("DELETE wymaga .where()");
            }
            ArrayList<Object> params = new ArrayList<Object>();
            params.add(this.whereValue);
            String sql = "DELETE FROM `" + this.tableName + "` WHERE `" + this.whereColumn + "` = ?";
            return new DatabaseQuery(sql, params);
        }
    }
}

