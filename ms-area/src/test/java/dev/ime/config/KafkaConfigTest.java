package dev.ime.config;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;


@ExtendWith(MockitoExtension.class)
class KafkaConfigTest {

	@InjectMocks
	private KafkaConfig kafkaConfig;

	@Test
	void topics_ReturnNewTopics() {
		
		KafkaAdmin.NewTopics topics = kafkaConfig.topics();
		
		org.junit.jupiter.api.Assertions.assertAll(
				() -> Assertions.assertThat(topics).isNotNull()
				);
		
	}

	@Test
	void reactiveKafkaProducerTemplate_ReturnTemplate() {		
		
		KafkaProperties kafkaProperties = new KafkaProperties();
		SslBundles sslBundles = Mockito.mock(SslBundles.class);
		
		ReactiveKafkaProducerTemplate<String, Object> result = kafkaConfig.reactiveKafkaProducerTemplate(kafkaProperties, sslBundles);
				
		org.junit.jupiter.api.Assertions.assertAll(
				() -> Assertions.assertThat(result).isNotNull()
				);	
	}
	
}
