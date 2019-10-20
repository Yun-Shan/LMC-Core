package org.yunshanmc.lmc.core.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class MockCommandSender {
    @Getter
    private final String name;

    private final Set<String> permissions = new HashSet<>();

    public void addPermissions(String... perms) {
        this.permissions.addAll(Arrays.asList(perms));
    }

    public void removePermissions(String... perms) {
        this.permissions.removeAll(Arrays.asList(perms));
    }

    public boolean hasPermission(String perm) {
        return this.permissions.contains(perm);
    }

}
