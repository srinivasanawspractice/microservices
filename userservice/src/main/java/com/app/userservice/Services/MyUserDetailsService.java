package com.app.userservice.Services;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.userservice.Models.UserPrincipal;
import com.app.userservice.Models.UserRegisteration;
import com.app.userservice.repo.UserRegisterationRepo;
@Service
public class MyUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserRegisterationRepo repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserRegisteration user =repo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("username not found "));
	
		
		//return new User(user.getUsername(), user.getPassword(),Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
		return new UserPrincipal(user);
	}

}
