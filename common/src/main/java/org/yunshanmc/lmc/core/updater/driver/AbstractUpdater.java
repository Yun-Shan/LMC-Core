package org.yunshanmc.lmc.core.updater.driver;

import org.yunshanmc.lmc.core.updater.Version;

public abstract class AbstractUpdater {
    public abstract Version checkVersion(String oldVersion);
}
