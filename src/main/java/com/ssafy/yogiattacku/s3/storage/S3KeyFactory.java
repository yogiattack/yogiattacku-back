package com.ssafy.yogiattacku.s3.storage;

import com.ssafy.yogiattacku.s3.config.AwsProps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3KeyFactory {
    private final AwsProps awsProps;

    public String postImageKey(UUID buecktRootKey, String fileExtension) {
        String prefix = awsProps.prefix();
        String extension = fileExtension.startsWith(".") ? fileExtension.substring(1) : fileExtension;
        return prefix + "/" + buecktRootKey + "/" + UUID.randomUUID() + "." + extension;
    }
}
