package dev.ime.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.ime.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByEmail(String email); 
	
}
