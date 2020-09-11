package org.yunshanmc.lmc.core.updater;

import java.util.Objects;

/**
 * @author Yun Shan
 */
public class Version {

    private final String name;
    private final String downloadUrl;

    public Version(String name, String downloadUrl) {
        this.name = name;
        this.downloadUrl = downloadUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Version version = (Version) o;
        return name.equals(version.name) &&
            Objects.equals(downloadUrl, version.downloadUrl);
    }

    public String getName() {
        return this.name;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, downloadUrl);
    }

    @Override
    public String toString() {
        return "Version{" +
            "name='" + name + '\'' +
            ", downloadUrl='" + downloadUrl + '\'' +
            '}';
    }
}
