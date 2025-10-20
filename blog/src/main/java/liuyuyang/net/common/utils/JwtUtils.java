package liuyuyang.net.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import liuyuyang.net.common.properties.JwtProperties;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    @Getter
    private static JwtProperties jwtProperties;

    public static void setJwtProperties(JwtProperties properties) {
        if (JwtUtils.jwtProperties == null) {
            JwtUtils.jwtProperties = properties;
        }
    }

    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param claims 设置的信�?     * @return
     */
    public static String createJWT(Map<String, Object> claims) {
        return createJWT(getJwtProperties().getSecretKey(), getJwtProperties().getTtl(), claims);
    }

    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信�?     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 指定签名的时候使用的签名算法，也就是header那部�?        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成JWT的时�?        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明�?                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘�?                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置过期时间
                .setExpiration(exp);

        return builder.compact();
    }

    /**
     * 获取签名秘钥
     */
    public static byte[] getSigningKey() {
        return getJwtProperties().getSecretKey().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Token解密
     *
     * @param token 加密后的token
     * @return
     */
    public static Claims parseJWT(String token) {
        // 得到DefaultJwtParser
        Claims claims = Jwts.parser()
                // 设置签名的秘�?                .setSigningKey(getSigningKey())
                // 设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
    }

}
