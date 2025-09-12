package com.app.userservice.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.userservice.Models.UserPrincipal;
import com.app.userservice.Models.UserRegisteration;
import com.app.userservice.dto.UserRegisterationDto;
import com.app.userservice.dto.UserRegisterationResponse;
import com.app.userservice.repo.UserRegisterationRepo;

@Service
public class UserRegisterationService {

	@Autowired
	private UserRegisterationRepo repo;

	@Autowired
	private AuthenticationManager manager;

	@Autowired
	private jwtService service;
	
	@Autowired
	private MyUserDetailsService myUserDetailsService;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

	public UserRegisterationResponse saveUser(UserRegisterationDto dto) {

		/*
		 * UserRegisteration user = new UserRegisteration();
		 * user.setUsername(dto.getUsername());
		 * user.setPassword(encoder.encode(dto.getPassword()));
		 * 
		 * 
		 * 
		 * 
		 * UserRegisteration saveduser = repo.save(user);
		 * 
		 * UserRegisterationResponse response = new UserRegisterationResponse();
		 * response.setId(saveduser.getId());
		 * response.setUsername(saveduser.getUsername());
		 * 
		 * return response;
		 */
		System.out.println("DTO ROLE = " + dto.getRole());

		UserRegisteration user = new UserRegisteration();
		user.setUsername(dto.getUsername());
		user.setPassword(encoder.encode(dto.getPassword()));
		user.setRole(dto.getRole()); // ✅ save role from DTO

		UserRegisteration savedUser = repo.save(user);

		UserRegisterationResponse response = new UserRegisterationResponse();
		response.setId(savedUser.getId());
		response.setUsername(savedUser.getUsername());
		response.setRole(savedUser.getRole());
		// you can also include role if you want in response
		return response;
	}

	public List<UserRegisteration> getAllUsers() {
		return repo.findAll();
	}

	public UserRegisteration getUserById(Integer id) {
		Optional<UserRegisteration> user = repo.findById(id);
		UserRegisteration userById = user.get();

		return userById;
	}

	public UserRegisteration updateUserById(Integer id, UserRegisterationDto dto) {
		Optional<UserRegisteration> user = repo.findById(id);
		UserRegisteration userById = user.get();
		userById.setPassword(encoder.encode(dto.getPassword()));
		userById.setUsername(dto.getUsername());
		repo.save(userById);
		return userById;
	}

	public UserRegisterationResponse deleteUserById(Integer id) {
		Optional<UserRegisteration> user = repo.findById(id);
		UserRegisteration userById = user.get();
		UserRegisterationResponse deleteduser = new UserRegisterationResponse();
		deleteduser.setId(userById.getId());
		deleteduser.setUsername(userById.getUsername());

		repo.deleteById(id);
		return deleteduser;
	}

	public ResponseEntity<Map<String, String>> generateTokens(UserRegisterationDto request) {
		try {
			Authentication authentication = manager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

			if (authentication.isAuthenticated()) {
				UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
				String accessToken = service.generateToken(userPrincipal);
				String refreshToken = service.generateRefreshToken(userPrincipal);
				return ResponseEntity.ok(Map.of("AccessToken", accessToken, "RefreshToken", refreshToken));
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid user credentials"));
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid user credentials"));
		}
	}

	public ResponseEntity<?> generateAccessToken(String refreshToken) {
		if(!service.isRefreshTokenValid(refreshToken)) {
			return ResponseEntity.status(401).body(" Refresh token expired or invalid. Please login again.");
		}
		
		String username=service.getUsernameFromRefreshToken(refreshToken);
		UserPrincipal user=(UserPrincipal) myUserDetailsService.loadUserByUsername(username);
		 String newAccessToken = service.generateToken(user);
		 
		 Map<String, String> response = new HashMap<>();
		    response.put("accessToken", newAccessToken);
		    response.put("refreshToken", refreshToken); // reuse existing refresh token

		    return ResponseEntity.ok(response);
		
		

	}

}
