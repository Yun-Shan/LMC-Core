package org.yunshanmc.lmc.core.updater;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Version {

    private final String name;
    private final String downloadUrl;
}
