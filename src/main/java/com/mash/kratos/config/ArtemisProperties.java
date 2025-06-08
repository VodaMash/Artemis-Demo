package com.mash.kratos.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "spring.artemis")
@Setter
@Getter
public class ArtemisProperties {
  private String brokerUrl;
  private String user;
  private String password;
}
