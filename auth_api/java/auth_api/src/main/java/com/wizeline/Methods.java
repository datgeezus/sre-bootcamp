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

  private static Optional<String> getRole(Connection connection, String userName, String password) {
    try (Statement statement = connection.createStatement();) {
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

  private static Connection getConnection() throws SQLException {
    String dbName = System.getenv("DB_NAME");
    String dbUsername = System.getenv("DB_USERNAME");
    String dbPassword = System.getenv("DB_PASSWORD");
    String hostName = System.getenv("DB_HOSTNAME");
    String port = System.getenv("DB_PORT");
    String url = String.format(
            "jdbc:mysql://%s:%s/%s?user=%s&password=%s?characterEncoding=utf8",
            hostName, port, dbName, dbUsername, dbPassword);
    return DriverManager.getConnection(url);
  }

  public static String generateToken(String username, String password) {
    return Optional.ofNullable(System.getenv("SECRET"))
            .map(secret -> generateToken(username, password, secret))
            .orElse("");
  }

  public static String generateToken(String username, String password, String secret) {
//    try (Connection connection = getConnection()) {
//      Optional<String> roleOpt = getRole(connection, username, password);
      Optional<String> roleOpt = Optional.of("admin");
      return roleOpt.map(role -> createJWT(role, secret)).orElse("");
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//
//    return "";
  }

  public static String accessData(String authorization){
    return Optional.ofNullable(System.getenv("SECRET"))
            .map(secret -> accessData(authorization, secret))
            .orElse("");
  }

  public static String accessData(String authorization, String secret){
    try {
      var claims = decodeJWT(authorization, secret);
      System.out.print(claims);
      return "You are under protected data";
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return "test";
  }

  private static Jws<Claims> decodeJWT(String jwt, String secret) {
//    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
//    SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jwt);
  }

  private static String createJWT(String role, String secret) {
//    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret.getBytes(StandardCharsets.UTF_8)));
//    SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8)));
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    return Jwts.builder()
            .setHeaderParams(Map.of("typ", "JWT"))
            .claim("role", role)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
  }
}

