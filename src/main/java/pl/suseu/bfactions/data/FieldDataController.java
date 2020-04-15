package pl.suseu.bfactions.data;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.data.database.Database;
import pl.suseu.bfactions.settings.FieldTier;
import pl.suseu.bfactions.settings.Settings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class FieldDataController {

    private final BFactions plugin;
    private final Database database;
    private final Settings settings;

    public FieldDataController(BFactions plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
        this.settings = plugin.getSettings();
    }

    public boolean loadFields() {
        if (!createTable()) {
            return false;
        }

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        String query = "select * from `" + database.getFieldsTableName() + "`;";

        database.executeQuery(query, resultSet -> {
            try {
                while (resultSet.next()) {
                    boolean loadSuccess = loadField(resultSet);
                    if (loadSuccess) {
                        success.getAndIncrement();
                    } else {
                        failure.getAndIncrement();
                    }
                }
            } catch (Exception e) {
                failure.getAndIncrement();
                e.printStackTrace();
            }
        });

        plugin.getLogger().info("Loaded " + success + " fields successfully.");
        if (failure.get() != 0) {
            plugin.getLogger().warning("Failed to load " + failure + " fields!");
        }
        return failure.get() == 0;
    }

    public boolean saveField(Field field) {
        String update = getInsert(field);
        for (String query : update.split(";")) {
            try {
                database.executeUpdate(query);
            } catch (Exception e) {
                plugin.getLogger().warning("[MySQL] Update: " + query);
                plugin.getLogger().warning("Could not save field to database");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean loadField(ResultSet result) throws SQLException {
        String uuidString = result.getString("uuid");
        int tierIndex = result.getInt("tier");
        double currentEnergy = result.getDouble("currentEnergy");

        if (uuidString == null) {
            this.plugin.getLogger().warning("Cannot load field uuid!");
            return false;
        }

        UUID uuid = UUID.fromString(uuidString);

        FieldTier tier = this.settings.fieldTiers.get(tierIndex);
        Field field = new Field(uuid, tier);
        field.setCurrentEnergy(currentEnergy);
        this.plugin.getFieldRepository().addField(field);

        return true;
    }

    private String getInsert(Field field) {
        StringBuilder sb = new StringBuilder();

        sb.append("insert into `" + database.getFieldsTableName() + "` ");
        sb.append("(`uuid`, `tier`, `currentEnergy`) values ( ");
        sb.append("'" + field.getUuid() + "',");
        sb.append("'" + field.getTier().getTier() + "',");
        sb.append("'" + field.getCurrentEnergy() + "')");
        sb.append(" on duplicate key update ");
        sb.append("`tier` = '" + field.getTier().getTier() + "',");
        sb.append("`currentEnergy` = '" + field.getCurrentEnergy() + "'");

        return sb.toString();
    }

    public boolean createTable() {
        StringBuilder sb = new StringBuilder();

        sb.append("create table if not exists ");
        sb.append("`").append(database.getFieldsTableName()).append("`");
        sb.append("(`uuid` varchar(36) not null,");
        sb.append("`tier` int,");
        sb.append("`currentEnergy` double,");
        sb.append("primary key (`uuid`));");

        return database.executeUpdate(sb.toString());
    }

}