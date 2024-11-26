package dev.ime.config;

public class GlobalConstants {
	
	private GlobalConstants() {
		super();
	}
	
	// Values
	public static final Long EXPIRATION_TIME = 3600L;

	// Patterns
	public static final String PATTERN_NAME_FULL = "^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ][a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\\s\\-\\.&,:]{1,49}$";	
    public static final String PATTERN_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    
	// Messages
	public static final String MSG_EMPTYTOKEN = "WTF Empty Token";
	public static final String MSG_PATTERN_SEVERE = "### [** eXception **] -> [%s] ###";
	public static final String MSG_PATTERN_INFO = "### [%s] -> [%s] -> [ %s ]";
	public static final String MSG_NODATA = "No data available";
	public static final String MSG_SEPARATOR_SEVERE =  " <==> ";
	
	// Models
	public static final String USER_CAT = "User";
	public static final String USER_EMAIL = "email";
		
	// Exceptions
	public static final String EX_USERNOTFOUND = "UsernameNotFoundException";
	public static final String EX_VALIDATION = "ValidationException";
	public static final String EX_VALIDATION_DESC = "Kernel Panic in validation process";
	public static final String EX_PLAIN = "Exception";
	public static final String EX_PLAIN_DESC = "Exception because the night is dark and full of terrors";
	public static final String EX_EMPTYRESPONSE = "EmptyResponseException";
	public static final String EX_EMPTYRESPONSE_DESC = "No freak out, just an Empty Response";
	public static final String EX_EMAILUSED = "EmailUsedException";
	public static final String EX_EX_EMAILUSED_DESC = "Email already in use";	
	public static final String EX_NORESOURCE = "NoResourceFoundException";
	public static final String EX_NORESOURCE_DESC = "No Resource Found Exception";
	

}
