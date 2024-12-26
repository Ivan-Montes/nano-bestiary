package dev.ime.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Creature {

	private UUID creatureId;
	private String creatureName;	
	private String creatureDescription;	
	private UUID areaId;
	
	public Creature() {
		super();
	}
	
	public Creature(UUID creatureId, String creatureName, String creatureDescription, UUID areaId) {
		super();
		this.creatureId = creatureId;
		this.creatureName = creatureName;
		this.creatureDescription = creatureDescription;
		this.areaId = areaId;
	}
	
	public UUID getCreatureId() {
		return creatureId;
	}
	public void setCreatureId(UUID creatureId) {
		this.creatureId = creatureId;
	}
	public String getCreatureName() {
		return creatureName;
	}
	public void setCreatureName(String creatureName) {
		this.creatureName = creatureName;
	}
	public String getCreatureDescription() {
		return creatureDescription;
	}
	public void setCreatureDescription(String creatureDescription) {
		this.creatureDescription = creatureDescription;
	}
	public UUID getAreaId() {
		return areaId;
	}
	public void setAreaId(UUID areaId) {
		this.areaId = areaId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(areaId, creatureDescription, creatureId, creatureName);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Creature other = (Creature) obj;
		return Objects.equals(areaId, other.areaId) && Objects.equals(creatureDescription, other.creatureDescription)
				&& Objects.equals(creatureId, other.creatureId) && Objects.equals(creatureName, other.creatureName);
	}
	
	@Override
	public String toString() {
		return "Creature [creatureId=" + creatureId + ", creatureName=" + creatureName + ", creatureDescription="
				+ creatureDescription + ", areaId=" + areaId + "]";
	}	
	
}
