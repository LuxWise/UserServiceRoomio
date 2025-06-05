package com.userService.service.Jwt;

import com.userService.model.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import io.jsonwebtoken.Claims;


@Service
public class JwtService {

    @Value("${jwt.secret.access}")
    private String SECRET_KEY_ACCESS_TOKEN;

    @Value("${jwt.secret.refresh}")
    private String SECRET_KEY_REFRESH_TOKEN;

    public String getAccessToken(Users user){
        return getAccessToken(new HashMap<>(), user);
    }

    private String getAccessToken(HashMap<String,Object> extraClaims, Users user) {
        extraClaims.put("email", user.getEmail());
        extraClaims.put("role", user.getRole().getRole_name());

        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(user.getId().toString())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
            .signWith(getKeyAccessToken(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String getRefreshToken(Users user) {
        return getRefreshToken(new HashMap<>(),user);
    }

    private String getRefreshToken(HashMap<String,Object> extraClaims, Users user) {
        extraClaims.put("email", user.getEmail());
        extraClaims.put("role", user.getRole().getRole_name());

        return Jwts
            .builder()
            .setSubject(user.getId().toString())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis()+10000*60*60*24))
            .signWith(getKeyRefreshToken(), SignatureAlgorithm.HS256)
            .compact();
    }


    private Key getKeyAccessToken() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY_ACCESS_TOKEN);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    private Key getKeyRefreshToken() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY_REFRESH_TOKEN);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserIdFromRefreshToken(String refreshToken) {
        Key key = getKeyRefreshToken();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        return claims.getSubject();
    }

}
