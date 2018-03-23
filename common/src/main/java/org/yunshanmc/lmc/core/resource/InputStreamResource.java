/*
 * Author: Yun-Shan
 * Date: 2017/06/14
 */
package org.yunshanmc.lmc.core.resource;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * 输入流资源.
 * <p>
 *
 * @author Yun-Shan
 */
public class InputStreamResource implements Resource {
    
    private final InputStream stream;

    public InputStreamResource(InputStream stream) {
        Objects.requireNonNull(stream);
        this.stream = stream;
    }
    
    @Override
    public URL getURL() {
        return null;
    }
    
    @Override
    public InputStream getInputStream() {
        return this.stream;
    }

    @Override
    protected void finalize() throws Throwable {
        this.stream.close();
    }
}
