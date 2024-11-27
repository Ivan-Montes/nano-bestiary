package dev.ime.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table( name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User implements UserDetails {
	
	private static final long serialVersionUID = -4695165278124465518L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column( nullable = false)
	private String name;
	
	@Column( nullable = false)
	private String lastname;
	
	@Column( nullable = false, unique = true)
	private String email;
	
	@Column( nullable = false)
	@ToString.Exclude
	private String password;

    @Enumerated(EnumType.STRING) 
    private Role role;
    
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return List.of(new SimpleGrantedAuthority((role.name()))); 
		
	}
	
	@Override
	public String getUsername() {
		
		return email;
		
	}
	
}
