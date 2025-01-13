package dev.ime.infrastructure.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import dev.ime.config.GlobalConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = GlobalConstants.CREATURE_DB)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CreatureJpaEntity {

	@Id
	@Column( value = GlobalConstants.CREATURE_ID_DB )
	private UUID creatureId;

	@Column( value = GlobalConstants.CREATURE_NAME_DB )
	private String creatureName;	
	
	@Column( value = GlobalConstants.CREATURE_DESC_DB )
	private String creatureDescription;

	@Column( value = GlobalConstants.AREA_ID_DB )
	private UUID areaId;
	
}
