package com.wizeline;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class Methods {

  private static Connection getRemoteConnection() {
    try {
      Class.forName("com.mysql.Driver");
      String dbName = System.getenv("DB_NAME");
      String userName = System.getenv("DB_USERNAME");
      String password = System.getenv("DB_PASSWORD");
      String hostName = System.getenv("DB_HOSTNAME");
      String port = System.getenv("DB_PORT");
      String jdbcUrl = "jdbc:mysql://" + hostName + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
      return DriverManager.getConnection(jdbcUrl);
    } catch ( SQLException e) {

    } catch (ClassNotFoundException e) {

    }

    return null;
  }
  public static String generateToken(String username, String password) {
    String jws = createJWT("admin");

    return jws;
  }
  public static String accessData(String authorization){

    try {
      var claims = decodeJWT(authorization);
      System.out.print(claims);
      return "You are under protected data";
    }
    catch (Exception e) {
      System.out.print(e.getMessage());
    }
    return "test";
  }

  private static Jws<Claims> decodeJWT(String jwt) {
    String secret = System.getenv("SECRET");
    //SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    var builder = Jwts.parserBuilder()
            .setSigningKey(key)
            .build();
    return builder.parseClaimsJws(jwt);
  }

  private static String createJWT(String role) {
    String secret = System.getenv("SECRET");

    //SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    return Jwts.builder()
            .claim("role", role)
            .setHeaderParams(Map.of("typ", "JWT"))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
  }
}

