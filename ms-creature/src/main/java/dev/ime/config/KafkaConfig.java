package dev.ime.config;

import java.util.Map;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

import reactor.kafka.sender.SenderOptions;

@Configuration
public class KafkaConfig {

	@Bean
	KafkaAdmin.NewTopics topics() {
		
		return new NewTopics(
				TopicBuilder.name(GlobalConstants.CREATURE_CREATED).build(),
				TopicBuilder.name(GlobalConstants.CREATURE_UPDATED).build(),
				TopicBuilder.name(GlobalConstants.CREATURE_DELETED).build()
				);
		
	}

    @Bean
    ReactiveKafkaProducerTemplate<String, Object> reactiveKafkaProducerTemplate(
            KafkaProperties kafkaProperties,
            SslBundles sslBundles) {
        
        Map<String, Object> props = kafkaProperties.buildProducerProperties(sslBundles);
       
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
        
    }
    
}
