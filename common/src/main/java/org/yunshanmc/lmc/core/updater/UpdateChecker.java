package org.yunshanmc.lmc.core.updater;

import lombok.RequiredArgsConstructor;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.MessageSender;
import org.yunshanmc.lmc.core.resource.InputStreamResource;
import org.yunshanmc.lmc.core.resource.ResourceManager;
import org.yunshanmc.lmc.core.updater.driver.AbstractUpdater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Consumer;


@RequiredArgsConstructor
public class UpdateChecker {

    private final AbstractUpdater updater;

    public void check(String oldVersion, ResourceManager resourceManager, MessageSender messageSender) {
        messageSender.infoConsole("updater.checking");
        this.check(oldVersion, version -> {
            if (version == null) {
                messageSender.warningConsole("fetch-latest-version-fail");
                return;
            }
            if (version.getName().equals(oldVersion)) {
                messageSender.infoConsole("is-latest-version");
                return;
            }
            messageSender.infoConsole("updater.downing-new-version", version.getName());
            String path;
            try {
                URL url = new URL(version.getDownloadUrl());
                String fileName = url.getFile();
                path = "update/" + fileName;
                // 若已存在同名文件则不进行下载
                if (resourceManager.getFolderResource(path) == null) {
                    InputStream inputStream = url.openConnection().getInputStream();
                    resourceManager.writeResource(path, new InputStreamResource(inputStream), true);
                }
            } catch (IOException e) {
                ExceptionHandler.handle(e);
                return;
            }
            messageSender.infoConsole("updater.downloaded-new-version", path);
        });

    }

    public void check(String oldVersion, Consumer<Version> newVersionHandle) {
        Version version = this.updater.checkVersion(oldVersion);
        newVersionHandle.accept(version);
    }
}
