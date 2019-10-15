package org.yunshanmc.lmc.core.bukkit.config;

import lombok.Data;
import org.junit.Test;
import org.yunshanmc.lmc.core.MockPlugin;
import org.yunshanmc.lmc.core.bukkit.EnvironmentUtil;
import org.yunshanmc.lmc.core.config.ConfigFactory;
import org.yunshanmc.lmc.core.config.ConfigField;
import org.yunshanmc.lmc.core.config.ConfigFile;

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
    @Data
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
    }
}
