package com.app.userservice.Controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.userservice.Services.UserRegisterationService;
import com.app.userservice.dto.UserRegisterationDto;
import com.app.userservice.dto.UserRegisterationResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	
	@Autowired
	private UserRegisterationService service;

	@PostMapping("/register")
	public UserRegisterationResponse userRegisteration(@RequestBody UserRegisterationDto dto ) {

		return service.saveUser(dto);
		
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody UserRegisterationDto request) {
	    

	    return service.generateTokens(request);

	    
	}
	
	@PostMapping("/refreshToken")
	public ResponseEntity<?> refreshToken(@RequestBody Map<String,String> request){
		
		String refreshToken =request.get("refreshToken");
		
		return service.generateAccessToken(refreshToken);
		
		
	}
	
}
