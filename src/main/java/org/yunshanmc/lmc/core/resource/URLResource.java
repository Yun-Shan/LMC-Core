/*
 * Author: Yun-Shan
 * Date: 2017/06/13
 */
package org.yunshanmc.lmc.core.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * URL资源
 */
public class URLResource implements Resource {
    
    private final URL url;
    
    private InputStream stream;
    
    public URLResource(URL url) {
        this.url = url;
    }
    
    @Override
    public URL getURL() {
        return this.url;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (this.stream == null) this.stream = this.url.openStream();
        return this.stream;
    }
}
