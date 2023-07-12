package com.mw.security.config;

import com.mw.security.token.TokenIssuer;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public String extractIss(String token) {
        return extractClaim(token, Claims::getIssuer);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(
                new HashMap<>(),
                userDetails,
                refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        extraClaims.putIfAbsent("iss", TokenIssuer.KCHAT.toString());
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String[] payload (String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        if (payload.startsWith("{")){
            payload = payload.substring(1,payload.length()-1);
        }
        String[] split = payload.split(",");
        return split;
    }

    public String getByKey(String[] strings, String key){
        key = "\""+key+"\"";
        for(String s : strings){
            if(s.trim().startsWith(key)){
                String[] split = s.split(":");
                String value =
                        split[1]
                                .trim()
                                .replace("^\"","")
                                .replace("\"$","");
                return value;
            }
        }
        return null;
    }

    public String getSub(String token){
        String[] payload = payload(token);
        String sub = getByKey(payload, "sub");
        return sub;
    }

    public String getIssuer(String token){
        String[] payload = payload(token);
            for(String s : payload){
                s = s.trim().toLowerCase();
                if(s.startsWith("\"iss\"")){
                    String issuer = s.split(":")[1].replaceAll("\"","");
                    return issuer;
                }
            }
        return "";
    }

    public boolean isTokenIssuedByKChat(String jwt) {
        String issuer = getIssuer(jwt);
        System.out.println("------- issuer -------" + issuer);
        System.out.println("------- must be -------" + TokenIssuer.KCHAT.toString().toLowerCase());
        System.out.println("------- is it -------" +
                issuer.equalsIgnoreCase(TokenIssuer.KCHAT.toString().toLowerCase()));

        return issuer.equalsIgnoreCase(TokenIssuer.KCHAT.toString().toLowerCase());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
      return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
      return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token)
            throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        Claims claims;
        claims = Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
