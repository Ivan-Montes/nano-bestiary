package dev.ime.application.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.config.GlobalConstants;


public class EntityAssociatedException  extends BasicException{	

	private static final long serialVersionUID = -4905083516375255960L;

	public EntityAssociatedException( Map<String, String> errors ) {
		super(
				UUID.randomUUID(), 
				GlobalConstants.EX_ENTITYASSOCIATED, 
				GlobalConstants.EX_ENTITYASSOCIATED_DESC,
				errors
				);
	}	
	
}
