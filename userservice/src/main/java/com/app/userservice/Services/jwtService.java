package com.app.userservice.Services;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.webauthn.api.Bytes;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.app.userservice.Models.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class jwtService {

	private static final String SECRET_KEY_STRING = "2cc2078406c4a02dcf0d89a7f5a36db12b106d4238795659ecac5af8f7779af08d5874be";
	private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

	public String generateToken(UserPrincipal userPrincipal) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", userPrincipal.getId());
		claims.put("role", userPrincipal.getRole().name()); // "ADMIN"
		return Jwts.builder().claims().add(claims).subject(userPrincipal.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5)).and()
				.signWith(SECRET_KEY, Jwts.SIG.HS256).compact();
	}

	public String generateRefreshToken(UserPrincipal userPrincipal) {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("id", userPrincipal.getId());
		claims.put("role", userPrincipal.getRole().name());
		return Jwts.builder().claims().add(claims).subject(userPrincipal.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)).and()
				.signWith(SECRET_KEY, Jwts.SIG.HS256).compact();
	}

	public String extractUserName(String token) {
		// extract the username from jwt token
		return extractClaim(token, Claims::getSubject);
	}

	public String extractRole(String token) {
		return extractAllClaims(token).get("role", String.class);
	}

	public Integer extractUserId(String token) {
		return extractAllClaims(token).get("id", Integer.class);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String userName = extractUserName(token);
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public boolean isRefreshTokenValid(String refreshToken) {
	    try {
	        Date expiration = extractClaim(refreshToken, Claims::getExpiration);
	        return expiration.after(new Date()); // ✅ valid if not expired
	    } catch (Exception e) {
	        return false; // invalid signature, malformed, etc.
	    }
	}

	public String getUsernameFromRefreshToken(String refreshToken) {
	    return extractUserName(refreshToken);
	}
}
