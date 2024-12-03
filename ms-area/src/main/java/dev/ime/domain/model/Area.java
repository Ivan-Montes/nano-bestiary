package dev.ime.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Area {

	private UUID areaId;
	private String areaName;	
	
	public Area() {
		super();
	}

	public Area(UUID areaId, String areaName) {
		super();
		this.areaId = areaId;
		this.areaName = areaName;
	}

	public UUID getAreaId() {
		return areaId;
	}

	public void setAreaId(UUID areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(areaId, areaName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Area other = (Area) obj;
		return Objects.equals(areaId, other.areaId) && Objects.equals(areaName, other.areaName);
	}

	@Override
	public String toString() {
		return "Area [areaId=" + areaId + ", areaName=" + areaName + "]";
	}
	
	
}
