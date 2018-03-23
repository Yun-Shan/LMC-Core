/*
 * Author: Yun-Shan
 * Date: 2017/06/13
 */
package org.yunshanmc.lmc.core.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarFile;

/**
 * URL资源.
 * <p>
 *
 * @author Yun-Shan
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
        try {
            if (this.stream == null) {
                this.stream = this.url.openStream();
            }
        } catch (FileNotFoundException e) {
            String path = this.url.getPath();
            int sep = path.indexOf("!/");
            /* file:/ */
            File jarFile = new File(path.substring(6, sep));
            JarFile jar = new JarFile(jarFile);
            this.stream = jar.getInputStream(jar.getEntry(path.substring(sep + 2)));
        }
        return this.stream;
    }

    @Override
    protected void finalize() throws Throwable {
        if (this.stream != null) {
            this.stream.close();
        }
    }
}
