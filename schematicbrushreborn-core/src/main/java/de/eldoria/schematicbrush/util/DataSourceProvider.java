package de.eldoria.schematicbrush.util;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.plugin.Plugin;
import org.mariadb.jdbc.MariaDbPoolDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class DataSourceProvider {

    public static DataSource initMySQLDataSource(Plugin plugin, Database database) {
        // We create a new SQL connection pool data source for MySQL.
        // However this data source will work with MariaDB as well.
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
        // we set our credentials
        dataSource.setServerName(database.getHost());
        dataSource.setPassword(database.getPassword());
        dataSource.setPortNumber(database.getPort());
        dataSource.setDatabaseName(database.getDatabase());
        dataSource.setUser(database.getUser());

        // Test connection
        try {
            testDataSource(plugin, dataSource);
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.getMessage());
        }

        // and return our stuff.
        return dataSource;
    }

    public static DataSource initMariaDBDataSource(Plugin plugin, Database database) {
        // We create a new SQL connection pool data source for MariaDB
        MariaDbPoolDataSource dataSource = new MariaDbPoolDataSource();
        // we set our credentials
        try {
            dataSource.setServerName(database.getHost());
            dataSource.setPassword(database.getPassword());
            dataSource.setPortNumber(database.getPort());
            dataSource.setDatabaseName(database.getDatabase());
            dataSource.setUser(database.getUser());
            dataSource.setMaxPoolSize(20);
            // Test connection
            testDataSource(plugin, dataSource);
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.getMessage());
        }

        // and return our stuff.
        return dataSource;
    }

    private static void testDataSource(Plugin plugin, DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(1000)) {
                throw new SQLException("Could not establish database connection.");
            }
        }
        if (plugin != null) {
            plugin.getLogger().info("§2Database connection established.");
        }
    }
}