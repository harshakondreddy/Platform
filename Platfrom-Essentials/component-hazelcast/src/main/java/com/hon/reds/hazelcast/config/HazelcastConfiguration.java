package com.hon.reds.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.ManagementCenterConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

  // Hazelcast Management center URL
  @Value("${hazelcast.management.console.url:http://dockerhost:18000/hazelcast-mancenter}")
  private String hazelcastMgmtConsoleUrl;

  @Bean
  public Config hazelCastConfig() {
    Config config = new Config();
    config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
    config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
    config
        .getNetworkConfig()
        .getJoin()
        .getEurekaConfig()
        .setEnabled(true)
        .setProperty("self-registration", "true")
        .setProperty("namespace", "hazelcast")
        .setProperty("use-metadata-for-host-and-port", "true");

    // Configuration to enable management center.
    ManagementCenterConfig mgmtconfig = new ManagementCenterConfig();
    mgmtconfig.setEnabled(Boolean.TRUE);
    mgmtconfig.setUrl(hazelcastMgmtConsoleUrl);
    config.setManagementCenterConfig(mgmtconfig);

    return config;
  }
}
