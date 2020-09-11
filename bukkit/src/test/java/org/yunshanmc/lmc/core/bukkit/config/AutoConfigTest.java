package org.yunshanmc.lmc.core.bukkit.config;

import org.junit.Test;
import org.yunshanmc.lmc.core.MockPlugin;
import org.yunshanmc.lmc.core.bukkit.EnvironmentUtil;
import org.yunshanmc.lmc.core.config.ConfigFactory;
import org.yunshanmc.lmc.core.config.ConfigField;
import org.yunshanmc.lmc.core.config.ConfigFile;

import java.util.Objects;

import static org.junit.Assert.*;

public class AutoConfigTest {

    @Test
    public void test() {
        EnvironmentUtil.mockBukkit();
        DatabaseConfig config = ConfigFactory.loadConfig(MockPlugin.newInstance().getConfigManager(), DatabaseConfig.class);
        assertNotNull(config);
        assertEquals("localhost", config.getHost());
        assertEquals(3306, config.getPort());
        assertEquals("root", config.getUsername());
        assertEquals("", config.getPassword());
        assertEquals("test", config.getDatabaseName());
    }

    @ConfigFile(section = "database")
    public static class DatabaseConfig {
        @ConfigField
        String host;
        @ConfigField
        int port;
        @ConfigField
        String username;
        @ConfigField
        String password;
        @ConfigField
        String databaseName;

        // region data


        public DatabaseConfig() {
        }

        public DatabaseConfig(String host, int port, String username, String password, String databaseName) {
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
            this.databaseName = databaseName;
        }

        public String getHost() {
            return this.host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return this.port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDatabaseName() {
            return this.databaseName;
        }

        public void setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DatabaseConfig config = (DatabaseConfig) o;
            return port == config.port &&
                Objects.equals(host, config.host) &&
                Objects.equals(username, config.username) &&
                Objects.equals(password, config.password) &&
                Objects.equals(databaseName, config.databaseName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(host, port, username, password, databaseName);
        }

        @Override
        public String toString() {
            return "DatabaseConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", databaseName='" + databaseName + '\'' +
                '}';
        }

        // endregion
    }
}
