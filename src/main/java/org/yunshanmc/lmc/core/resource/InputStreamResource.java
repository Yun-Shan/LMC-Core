/*
 * Author: Yun-Shan
 * Date: 2017/06/14
 */
package org.yunshanmc.lmc.core.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * //TODO
 */
public class InputStreamResource implements Resource {
    
    private final InputStream stream;
    
    public InputStreamResource(InputStream stream) {
        this.stream = stream;
    }
    
    @Override
    public URL getURL() {
        return null;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return this.stream;
    }
}
