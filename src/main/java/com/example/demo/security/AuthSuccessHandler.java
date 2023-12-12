package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.security.user.JwtUser;
import com.example.demo.security.user.JwtUserService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final int expTime;

	private final String secret;
	private final JwtUserService jwtUserService;


	public AuthSuccessHandler(@Value("${jwt.expiration}") int expTime,
							  @Value("${jwt.secret}") String secret,
							  JwtUserService jwtUserService) {
		this.expTime = expTime;
		this.secret = secret;
		this.jwtUserService = jwtUserService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		UserDetails principal = (UserDetails) authentication.getPrincipal();
		JwtUser user = jwtUserService.getByUsername(principal.getUsername());
		String token = JWT.create()
				.withSubject(user.getEmail())
				.withExpiresAt(Instant.ofEpochMilli(ZonedDateTime.now(ZoneId.systemDefault()).toInstant().toEpochMilli() + expTime))
				.sign(Algorithm.HMAC256(secret));
		response.addHeader("Authorization", "Bearer " + token);
		response.addHeader("Content-type", "application/json");
		Set<String> roles = principal.getAuthorities().stream().map(Object::toString).collect(Collectors.toSet());
		JSONObject object = new JSONObject();
		object.put("token", token);
		object.put("position", user.getPosition());
		object.put("username", user.getUsername());
		object.put("id", user.getId());
		object.put("roles", roles);
		response.getWriter().write(object.toString());
	}

}
