package org.yunshanmc.lmc.core.config;

import org.yunshanmc.lmc.core.resource.FileResource;
import org.yunshanmc.lmc.core.resource.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * @author Yun-Shan
 */
public abstract class BaseLMCConfiguration implements LMCConfiguration {

    protected final Resource resource;
    private boolean isRoot;

    public BaseLMCConfiguration(Resource resource) {
        this.resource = resource;
        this.isRoot = true;
    }

    protected BaseLMCConfiguration() {
        this(null);
        this.isRoot = false;
    }

    @Override
    public void save() throws IOException {
        if (!this.isRoot) {
            // 虽然把saveHandle层层传递就能做到在每一层都可以保存，但是这样逻辑上好像会乱，就禁止这样做
            throw new UnsupportedOperationException("only root config can save.");
        }
        if (!(this.resource instanceof FileResource)) {
            // 只有插件文件夹的配置可写，插件本体的或者其它的都不行
            throw new UnsupportedOperationException("only config in plugin folder can save.");
        }
        Files.write(((FileResource) this.resource).getFile().toPath(),
            this.saveToString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
    }

    @Override
    public final void reload() throws IOException {
        if (!this.isRoot) {
            throw new UnsupportedOperationException("only root config can reload.");
        }
        this.reload0();
    }

    /**
     * 重载配置文件
     *
     * @throws IOException 读取文件时出现异常
     */
    protected abstract void reload0() throws IOException;
}
