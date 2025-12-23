package com.ssafy.yogiattacku.s3.storage;

import com.ssafy.yogiattacku.s3.config.CdnProps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CdnUrlBuilder {
    private final CdnProps cdnProps;

    public String toPublishUrl(String s3Key) {
        String base = stripTrailingSlash(cdnProps.baseUrl());
        String path = stripLeadingSlash(s3Key);
        return base + "/" + path;
    }

    private String stripTrailingSlash(String url) {
        return (url != null && url.endsWith("/")) ? url.substring(0, url.length() - 1) : url;
    }

    private String stripLeadingSlash(String url) {
        return (url != null && url.startsWith("/")) ? url.substring(1) : url;
    }
}
