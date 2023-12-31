package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.security.user.JwtUserDetailsService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonAuthorizationFilter extends BasicAuthenticationFilter {
	private static final String TOKEN_PREFIX = "Bearer ";
	private final JwtUserDetailsService jwtUserDetailsService;
	private final String secret;

	public JsonAuthorizationFilter(AuthenticationManager authenticationManager, JwtUserDetailsService jwtUserDetailsService, String secret) {
		super(authenticationManager);
		this.jwtUserDetailsService = jwtUserDetailsService;
		this.secret = secret;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		UsernamePasswordAuthenticationToken auth = getAuthentication(request);
		if (auth == null) {
			filterChain.doFilter(request, response);
			return;
		}
		SecurityContextHolder.getContext().setAuthentication(auth);
		filterChain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (token == null || token.startsWith(TOKEN_PREFIX)) {
			return null;
		}
		String email = JWT.require(Algorithm.HMAC256(secret))
				.build()
				.verify(token.replace(TOKEN_PREFIX, ""))
				.getSubject();
		if (email == null) return null;
		UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email);
		return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
	}
}
