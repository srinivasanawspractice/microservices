package com.app.userservice.Controllers;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;import org.springframework.web.client.HttpClientErrorException.Forbidden;

import com.app.userservice.Models.UserRegisteration;
import com.app.userservice.Services.UserRegisterationService;
import com.app.userservice.dto.UserRegisterationDto;
import com.app.userservice.dto.UserRegisterationResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
	
	@Autowired
	private UserRegisterationService service;

	
	
	
	
	
	@GetMapping("/getUserById/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Integer id,HttpServletRequest req) {
		
		Integer loggedInUserId=(Integer) req.getAttribute("userId");
		
		if(loggedInUserId != id) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error","you can fetch only your profile info alone "));
		}
		
		UserRegisteration userInfo=service.getUserById(id);
		
		Map<String,Object> response =Map.of(
				"id",userInfo.getId(),
				"username",userInfo.getUsername(),
				"role",userInfo.getRole(),
				"message","UserInfo is fetched successfully"
				);
		
		return ResponseEntity.ok(response);
	}
	/*
	 * @GetMapping("/getuserbyid/{id}") public UserRegisteration
	 * getUserById(@PathVariable Integer id) { return service.getUserById(id); }
	 */

	/*
	 * @GetMapping("/getuserbyid") public UserRegisteration
	 * getUserById(@RequestParam Integer id) { return service.getUserById(id); }
	 */
	
	@PutMapping("/updateById/{id}")
	public ResponseEntity<?> updateUserById(@PathVariable Integer id,@RequestBody UserRegisterationDto dto ,HttpServletRequest req) {
		
		Integer loggedinUserid=(Integer) req.getAttribute("userId");
		
		if(loggedinUserid != id) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error","you can update only your profile info alone "));
		}
		
		UserRegisteration updatedUser=service.updateUserById(id,dto);
		
		Map<String,Object> response =Map.of(
				"id",updatedUser.getId(),
				"Name",updatedUser.getUsername(),
				"role",updatedUser.getRole(),
				"message","profile updated successfully"
				);
		
		return ResponseEntity.ok(response);
	}
	
	
	
	
}
