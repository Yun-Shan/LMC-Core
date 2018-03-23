/*
 * Author: Yun-Shan
 * Date: 2017/06/14
 */
package org.yunshanmc.lmc.core.resource;

import org.yunshanmc.lmc.core.exception.ExceptionHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * 文件资源.
 * <p>
 *
 * @author Yun-Shan
 */
public class FileResource implements Resource {
    
    private final File file;
    
    public FileResource(File file) {
        Objects.requireNonNull(file);
        this.file = file;
    }
    
    @Override
    public URL getURL() {
        try {
            return this.file.toURI().toURL();
        } catch (MalformedURLException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }
    
    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(this.file);
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }
}
