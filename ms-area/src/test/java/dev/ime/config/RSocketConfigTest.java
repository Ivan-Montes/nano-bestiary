package dev.ime.config;


import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.rsocket.context.RSocketServerInitializedEvent;
import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.ApplicationListener;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClient;

@ExtendWith(MockitoExtension.class)
class RSocketConfigTest {

    @Mock
    private EurekaInstanceConfigBean eurekaInstanceConfig;

    @Mock
    private EurekaClient eurekaClient;

    @Mock
    private ApplicationInfoManager applicationInfoManager;

    @InjectMocks
    private RSocketConfig rSocketConfig;
    
    
	@Test
	void rSocketServerInitializedListener_ReturnListener() {
		
		
		ApplicationListener<RSocketServerInitializedEvent> listener = rSocketConfig.rSocketServerInitializedListener();
		
		org.junit.jupiter.api.Assertions.assertAll(
        		()-> Assertions.assertThat(listener).isNotNull()
        		);
	}
    
    @Test
    void rSocketServerInitializedListener_ShouldUpdateMetadataAndForceRegistration() {
        
        // Arrange
    	int testPort = 8000;
        RSocketServer mockServer = Mockito.mock(RSocketServer.class);
        InetSocketAddress mockAddress = new InetSocketAddress(testPort);
        Mockito.when(mockServer.address()).thenReturn(mockAddress);

        RSocketServerInitializedEvent mockEvent = Mockito.mock(RSocketServerInitializedEvent.class);
        Mockito.when(mockEvent.getServer()).thenReturn(mockServer);

        Map<String, String> initialMetadata = new HashMap<>();
        Mockito.when(eurekaInstanceConfig.getMetadataMap()).thenReturn(initialMetadata);

        Mockito.when(eurekaClient.getApplicationInfoManager()).thenReturn(applicationInfoManager);

        // Act
        ApplicationListener<RSocketServerInitializedEvent> listener = rSocketConfig.rSocketServerInitializedListener();
        listener.onApplicationEvent(mockEvent);

        // Assert
        Mockito.verify(eurekaInstanceConfig).getMetadataMap();
        @SuppressWarnings("unchecked")
		ArgumentCaptor<Map<String, String>> metadataCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(eurekaInstanceConfig).setMetadataMap(metadataCaptor.capture());
        
        Map<String, String> capturedMetadata = metadataCaptor.getValue();
        Assertions.assertThat(capturedMetadata).containsEntry(GlobalConstants.RSOCKET_PORT, String.valueOf(testPort));

        Mockito.verify(applicationInfoManager).registerAppMetadata(capturedMetadata);
    }
    
}
