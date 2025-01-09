package dev.ime.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.rsocket.context.RSocketServerInitializedEvent;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.discovery.EurekaClient;

@Configuration
public class RSocketConfig {
    
    private final EurekaInstanceConfigBean eurekaInstanceConfig;
    private final EurekaClient eurekaClient;
    
    public RSocketConfig(EurekaInstanceConfigBean eurekaInstanceConfig, EurekaClient eurekaClient) {
		super();
		this.eurekaInstanceConfig = eurekaInstanceConfig;
		this.eurekaClient = eurekaClient;
	}

	@Bean
    ApplicationListener<RSocketServerInitializedEvent> rSocketServerInitializedListener() {
        return event -> {
            int rsocketPort = event.getServer().address().getPort();
            updateEurekaMetadata(rsocketPort);
        };
    }

    private void updateEurekaMetadata(int rsocketPort) {
        Map<String, String> metadata = new HashMap<>(eurekaInstanceConfig.getMetadataMap());
        metadata.put(GlobalConstants.RSOCKET_PORT, String.valueOf(rsocketPort));
        eurekaInstanceConfig.setMetadataMap(metadata);
        
        forceEurekaRegistration(metadata);
    }
    
    private void forceEurekaRegistration(Map<String, String> metadata) {
        eurekaClient.getApplicationInfoManager().registerAppMetadata(metadata);
    }
    
}

