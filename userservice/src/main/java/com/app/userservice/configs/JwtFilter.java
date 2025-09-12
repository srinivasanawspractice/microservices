package com.app.userservice.configs;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.userservice.Services.MyUserDetailsService;
import com.app.userservice.Services.jwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private jwtService service;

	@Autowired
	ApplicationContext context;
	
	@Autowired
	private MyUserDetailsService userdetailService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {
	    try {
	        String authHeader = request.getHeader("Authorization");
	        System.out.println("Authorization Header = " + authHeader);
	        String token = null;
	        String userName = null;

	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            token = authHeader.substring(7);
	            System.out.println("Extracted token: " + token);
	            userName = service.extractUserName(token);
	            System.out.println("Username from Token = " + userName);
	        }

	        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            UserDetails userDetails = userdetailService.loadUserByUsername(userName);

	            if (service.validateToken(token, userDetails)) {

	                Integer userId = service.extractUserId(token);
	                String role = service.extractRole(token);

	                System.out.println("UserId from Token = " + userId);
	                System.out.println("Roles from Token = " + role);

	                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

	                UsernamePasswordAuthenticationToken authToken =
	                        new UsernamePasswordAuthenticationToken(
	                                userDetails,
	                                null,
	                                authorities);

	                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	                request.setAttribute("userId", userId);  // optional

	                SecurityContextHolder.getContext().setAuthentication(authToken);
	                
	              
	                System.out.println("Authentication set in context: " + SecurityContextHolder.getContext().getAuthentication());

	            }

	        }
	    } catch (Exception ex) {
	        System.out.println("JWT Filter Exception: " + ex.getMessage());
	        // Optional: you could also short-circuit with 401 here
	        // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
	        // return;
	    }

	    filterChain.doFilter(request, response);
	}

}
