package com.ssafy.yogiattacku.s3.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@Validated
@ConfigurationProperties(prefix = "app.aws")
public record AwsProps(@NotNull String region, @NotNull String bucket, @NotNull String prefix) {

}
