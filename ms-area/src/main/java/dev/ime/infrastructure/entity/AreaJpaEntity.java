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

@Table(name = GlobalConstants.AREA_DB)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AreaJpaEntity {

	@Id
	@Column( value = GlobalConstants.AREA_ID_DB )
	private UUID areaId;

	@Column( value = GlobalConstants.AREA_NAME_DB )
	private String areaName;	
	
}
