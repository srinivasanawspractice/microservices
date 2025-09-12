package com.app.userservice.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.userservice.Models.UserRegisteration;
import com.app.userservice.Services.UserRegisterationService;
import com.app.userservice.dto.UserRegisterationDto;
import com.app.userservice.dto.UserRegisterationResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
	@Autowired
	private UserRegisterationService service;

	
	
	@GetMapping("/getAllUsers")
	public List<UserRegisteration> getAllUsers(){
		return service.getAllUsers();
	}
	
	
	@GetMapping("/getUserById/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Integer id) {
		
		
		
		UserRegisteration userInfo=service.getUserById(id);
		
		Map<String,Object> response =Map.of(
				"id",userInfo.getId(),
				"username",userInfo.getUsername(),
				"role",userInfo.getRole(),
				"message","UserInfo is fetched successfully"
				);
		
		return ResponseEntity.ok(response);
	}
	
	
	@PutMapping("/updateById/{id}")
	public ResponseEntity<?> updateUserById(@PathVariable Integer id,@RequestBody UserRegisterationDto dto) {
		
		
		
		UserRegisteration updatedUser=service.updateUserById(id,dto);
		
		Map<String,Object> response =Map.of(
				"id",updatedUser.getId(),
				"Name",updatedUser.getUsername(),
				"Password",updatedUser.getPassword(),
				"role",updatedUser.getRole(),
				"message","profile updated successfully"
				);
		
		return ResponseEntity.ok(response);
	}
	
	
	@DeleteMapping("/deleteUserbyid")
	 public UserRegisterationResponse deleteUserById(@RequestParam Integer id) { 
		return service.deleteUserById(id); 
		}
}
