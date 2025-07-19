package com.github.konstantyn111.crashapi.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT令牌服务
 * <p>
 * 负责JWT令牌的生成、验证和解析，支持访问令牌和刷新令牌的双重管理
 */
@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey signingKey;
    private final long jwtExpiration;

    /**
     * 获取刷新令牌有效期（毫秒）
     */
    @Getter
    private final long jwtRefreshExpiration;

    /**
     * 初始化JWT服务
     * @param secretKey BASE64编码的密钥
     * @param jwtExpiration 访问令牌有效期（毫秒）
     * @param jwtRefreshExpiration 刷新令牌有效期（毫秒）
     */
    public JwtService(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.expiration}") long jwtExpiration,
            @Value("${jwt.refresh-expiration}") long jwtRefreshExpiration
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiration = jwtExpiration;
        this.jwtRefreshExpiration = jwtRefreshExpiration;
    }

    /**
     * 从令牌中提取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从令牌中提取指定声明
     * @param token JWT令牌
     * @param claimsResolver 声明解析函数
     * @return 声明值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 生成访问令牌
     * @param userDetails 用户认证信息
     * @return JWT访问令牌
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * 生成带额外声明的访问令牌
     * @param extraClaims 自定义声明
     * @param userDetails 用户认证信息
     * @return JWT访问令牌
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(signingKey)
                .compact();
    }

    /**
     * 验证令牌有效性
     * @param token 待验证令牌
     * @param userDetails 用户认证信息
     * @return 有效返回true，否则false
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && isTokenExpired(token);
        } catch (SignatureException ex) {
            logger.error("无效的JWT签名: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("无效的JWT令牌: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("JWT令牌已过期: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("不支持的JWT令牌: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT声明为空: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error("意外的JWT错误: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * 验证刷新令牌有效性（忽略用户匹配）
     * @param token 刷新令牌
     * @return 有效返回true，否则false
     */
    public boolean isRefreshTokenValid(String token) {
        try {
            return isTokenExpired(token);
        } catch (Exception e) {
            logger.error("刷新令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查令牌是否过期
     * @return 未过期返回true，否则false
     */
    private boolean isTokenExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    /**
     * 提取令牌过期时间
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 解析令牌中的所有声明
     * @throws JwtException 令牌无效时抛出
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 生成刷新令牌
     * @param userDetails 用户认证信息
     * @return JWT刷新令牌
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpiration))
                .signWith(signingKey)
                .compact();
    }
}