/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.security.jwt;

import static com.github.hantsy.ee8sample.Constants.REMEMBERME_VALIDITY_SECONDS;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author hantsy
 */
@ApplicationScoped
public class TokenProvider {

    @Inject
    Logger LOGGER;

    private static final String AUTHORITIES_KEY = "auth";

    private String secretKey;

    private long tokenValidity;

    private long tokenValidityForRememberMe;

    @PostConstruct
    public void init() {
        // load from config
        this.secretKey = "my-secret-jwt-key";
        this.tokenValidity = TimeUnit.HOURS.toMillis(10);   //10 hours
        this.tokenValidityForRememberMe = TimeUnit.SECONDS.toMillis(REMEMBERME_VALIDITY_SECONDS);   //24 hours
    }

    public String createToken(String username, Set<String> authorities, Boolean rememberMe) {
        long now = (new Date()).getTime();
        long validity = rememberMe ? tokenValidityForRememberMe : tokenValidity;

        return Jwts.builder()
                .setSubject(username)
                .claim(AUTHORITIES_KEY, authorities.stream().collect(joining(",")))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setExpiration(new Date(now + validity))
                .compact();
    }

    public JwtCredential getCredential(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        Set<String> authorities
                = Arrays.asList(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .stream()
                        .collect(Collectors.toSet());

        return new JwtCredential(claims.getSubject(), authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            LOGGER.log(Level.INFO, "Invalid JWT signature: {0}", e.getMessage());
            return false;
        }
    }
}
