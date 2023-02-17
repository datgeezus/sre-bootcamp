package com.wizeline;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class Methods {

  private static Optional<String> getRole(String userName, String password) {
      String dbName = System.getenv("DB_NAME");
      String dbUsername = System.getenv("DB_USERNAME");
      String dbPassword = System.getenv("DB_PASSWORD");
      String hostName = System.getenv("DB_HOSTNAME");
      String port = System.getenv("DB_PORT");
      String url = String.format(
              "jdbc:mysql://%s:%s/%s?user=%s&password=%s?characterEncoding=utf8",
              hostName, port, dbName, dbUsername, dbPassword);
    try (Connection connection = DriverManager.getConnection(url); Statement statement = connection.createStatement();) {
      String query = String
              .format(
                      "SELECT user, role FROM users WHERE username = %s AND password = SHA2(CONCAT(%s, salt, 512))",
                      userName, password);
      ResultSet resultSet = statement.executeQuery(query);
      try {
        return Optional.of(resultSet.getString("role"));
      } catch ( SQLException e) {
        e.printStackTrace();
      }
    } catch ( SQLException e) {
      e.printStackTrace();
    }

    return Optional.empty();
  }

  public static String generateToken(String username, String password) {
    Optional<String> roleOpt = getRole(username, password);
    return roleOpt.map(Methods::createJWT).orElse("");
  }

  public static String accessData(String authorization){

    try {
      var claims = decodeJWT(authorization);
      System.out.print(claims);
      return "You are under protected data";
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return "test";
  }

  private static Jws<Claims> decodeJWT(String jwt) {
    String secret = System.getenv("SECRET");
    //SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jwt);
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

