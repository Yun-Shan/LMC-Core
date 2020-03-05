package org.yunshanmc.lmc.core.updater;

import org.junit.Test;
import org.yunshanmc.lmc.core.updater.driver.GithubUpdater;

public class UpdateCheckerTest {

    @Test
    public void run() throws Exception {
        String oldVersion = "0.0.1-alpha Build 29";
        new UpdateChecker(
            new GithubUpdater("Yun-Shan", "LMC-Core", (a, b) -> !a.equals(b),
                assetName -> assetName.startsWith("LMC-Core-Bukkit") && assetName.endsWith(".jar")
                    && !assetName.endsWith("javadoc.jar") && !assetName.endsWith("sources.jar")
            )).check(oldVersion, version -> {
            if (version == null) {
                System.err.println("获取最新版本失败！");
                return;
            }
            if (version.getName().equals(oldVersion)) {
                System.out.println("已经是最新版本了！");
                return;
            }
            System.out.println("发现新版本：" + version.getName());
            System.out.println("正在下载：" + version.getDownloadUrl());
        });
    }
}