package com.app.userservice.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.userservice.Models.Role;
import com.app.userservice.Models.UserRegisteration;

@Repository
public interface UserRegisterationRepo extends JpaRepository<UserRegisteration, Integer>{

	Optional<UserRegisteration> findByUsername(String username);

	
	Optional<UserRegisteration> findByRole(Role role);
	
	
}