package dev.ime.infrastructure.repository;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import dev.ime.config.GlobalConstants;
import dev.ime.infrastructure.entity.AreaJpaEntity;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Testcontainers
@DataR2dbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AreaReadRepositoryTest {

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;
    
    @Autowired
    private AreaReadRepository areaReadRepository;
    
    @SuppressWarnings("resource")
	@Container
	@ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))   
    .withInitScript("init.sql");

	private final UUID areaId01 = UUID.randomUUID();
	private final String areaName01 = "";

	@BeforeEach
	void setUp() {
		
		 StepVerifier
		 .create(r2dbcEntityTemplate.insert(new AreaJpaEntity(areaId01, areaName01)))
         .expectNextCount(1)
         .verifyComplete();
     
	}

    @AfterEach
    void tearDown() {
    	
        StepVerifier
        .create(r2dbcEntityTemplate.delete(AreaJpaEntity.class)
            .all()
            .then())
        .verifyComplete();
        
    }    

    @Test
    void connectionEstablished() {
      
        org.junit.jupiter.api.Assertions.assertAll(
    			()->Assertions.assertThat(postgres.isCreated()).isTrue(),
    			()->Assertions.assertThat(postgres.isRunning()).isTrue()
    			);
        
    }

    @Test
    void findAll_ShouldReturnAll() {
    	
        StepVerifier
        .create(areaReadRepository.findAll())
            .expectNextCount(1)
            .verifyComplete();
        
    }

    @Test
    void findById_ShouldReturnOneResult() {
    	
        StepVerifier
        .create(areaReadRepository.findById(areaId01))
            .assertNext(entity -> {
           
            org.junit.jupiter.api.Assertions.assertAll(
        			()->Assertions.assertThat(entity.getAreaId()).isEqualTo(areaId01),
        			()->Assertions.assertThat(entity.getAreaName()).isEqualTo(areaName01)
        			);
            })
            
            .verifyComplete();
        
    }

    @Test
    void findById_WithNonExistentId_ShouldReturnEmpty() {
    	
        UUID eXistenZId = UUID.randomUUID();
        StepVerifier.create(areaReadRepository.findById(eXistenZId))
            .verifyComplete();
        
    }

    @Test
    void findAll_WithMultipleEntities_ShouldReturnAllEntities() {
        
        Flux<AreaJpaEntity> insertFlux = Flux.just(
            createEntity(),
            createEntity(),
            createEntity()
        ).flatMap(entity -> r2dbcEntityTemplate.insert(entity));

        StepVerifier.create(insertFlux)
            .expectNextCount(3)
            .verifyComplete();

        StepVerifier.create(areaReadRepository.findAll())
            .expectNextCount(4)
            .verifyComplete();
        
    }

    @Test
    void findByName_WithExistentOne_ReturnIt() {
    	
    	StepVerifier.create(areaReadRepository.findByAreaName(areaName01))
    	.assertNext( entityFound -> {
    		org.junit.jupiter.api.Assertions.assertAll(
    				()->Assertions.assertThat(entityFound.getAreaName()).isEqualTo(areaName01)
    				);
    	})
    	.verifyComplete();
    	
    }
    
    private AreaJpaEntity createEntity() {
    	
        return new AreaJpaEntity( UUID.randomUUID(), generateRandomName() );
   
    }
    
	private String generateRandomName() {
		
		return GlobalConstants.AREA_CAT + ":" + UUID.randomUUID().toString().substring(0, 8);
	
	}
	
}
