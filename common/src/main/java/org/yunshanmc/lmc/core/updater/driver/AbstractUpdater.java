package org.yunshanmc.lmc.core.updater.driver;

import org.yunshanmc.lmc.core.updater.Version;

/**
 * @author Yun Shan
 */
public abstract class AbstractUpdater {
    public abstract Version checkVersion(String oldVersion);
}
