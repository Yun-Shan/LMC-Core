package org.yunshanmc.lmc.core.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MockCommandSender {

    private final String name;

    private final Set<String> permissions = new HashSet<>();

    public MockCommandSender(String name) {
        this.name = name;
    }

    public void addPermissions(String... perms) {
        this.permissions.addAll(Arrays.asList(perms));
    }

    public void removePermissions(String... perms) {
        this.permissions.removeAll(Arrays.asList(perms));
    }

    public boolean hasPermission(String perm) {
        return this.permissions.contains(perm);
    }

    public String getName() {
        return this.name;
    }
}
