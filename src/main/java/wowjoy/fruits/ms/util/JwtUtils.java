package wowjoy.fruits.ms.util;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.HmacUtils;

import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;

/**
 * Created by wangziwen on 2017/10/19.
 */
public class JwtUtils {

    public static String token(Jwt.Header header, Jwt.PayLoad payLoad) {
        return Jwt.newHmacSHA256(header, payLoad).toString();
    }

    public static Jwt.Header newHeader() {
        return Jwt.Header.newInstance();
    }

    public static Jwt.PayLoad newPayLoad(String userId, LocalDate exp, LocalDate iat) {
        return Jwt.PayLoad.newInstance(userId, exp, iat);
    }

    public static class Jwt {
        private final Header header;
        private final PayLoad payload;
        private final String signature;
        private final String salt;

        private Jwt(Header header, PayLoad payload, String salt) {
            this.header = header;
            this.payload = payload;
            this.salt = Base64.getUrlEncoder().encodeToString(salt.getBytes());
            this.signature = this.encryptHmacSHA256();
        }

        String encryptHmacSHA256() {
            return HmacUtils.hmacSha256Hex(salt, getHeader() + "." + getPayload());
        }

        public String getHeader() {
            return StringUtils.newStringUtf8(Base64.getUrlEncoder().encode(new Gson().toJsonTree(header).toString().getBytes()));
        }

        public String getPayload() {
            return StringUtils.newStringUtf8(Base64.getUrlEncoder().encode(new Gson().toJsonTree(payload).toString().getBytes()));
        }

        public String toString() {
            return this.getHeader() + "." + this.getPayload() + "." + this.signature;
        }

        public static Jwt newHmacSHA256(Header header, PayLoad payLoad) {
            return new Jwt(header, payLoad, "wangziwen");
        }

        public static class Header {
            /**
             * type: 必需。token 类型，JWT 表示是 JSON Web Token.
             * alg: 必需。token 所使用的签名算法，可用的值在 这⤵️有规定。
             * +--------------+-------------------------------+--------------------+
             * | "alg" Param  | Digital Signature or MAC      | Implementation     |
             * | Value        | Algorithm                     | Requirements       |
             * +--------------+-------------------------------+--------------------+
             * | HS256        | HMAC using SHA-256            | Required           |
             * | HS384        | HMAC using SHA-384            | Optional           |
             * | HS512        | HMAC using SHA-512            | Optional           |
             * | RS256        | RSASSA-PKCS1-v1_5 using       | Recommended        |
             * |              | SHA-256                       |                    |
             * | RS384        | RSASSA-PKCS1-v1_5 using       | Optional           |
             * |              | SHA-384                       |                    |
             * | RS512        | RSASSA-PKCS1-v1_5 using       | Optional           |
             * |              | SHA-512                       |                    |
             * | ES256        | ECDSA using P-256 and SHA-256 | Recommended+       |
             * | ES384        | ECDSA using P-384 and SHA-384 | Optional           |
             * | ES512        | ECDSA using P-521 and SHA-512 | Optional           |
             * | PS256        | RSASSA-PSS using SHA-256 and  | Optional           |
             * |              | MGF1 with SHA-256             |                    |
             * | PS384        | RSASSA-PSS using SHA-384 and  | Optional           |
             * |              | MGF1 with SHA-384             |                    |
             * | PS512        | RSASSA-PSS using SHA-512 and  | Optional           |
             * |              | MGF1 with SHA-512             |                    |
             * | none         | No digital signature or MAC   | Optional           |
             * |              | performed                     |                    |
             * +--------------+-------------------------------+--------------------+
             */
            private final String typ;
            private final String alg;

            private Header(String typ, String alg) {
                this.typ = typ;
                this.alg = alg;
            }

            public static Header newInstance() {
                return new Header("JWT", "SHA256");
            }
        }

        public static class PayLoad {
            /**
             * iss: The issuer of the token，token 是给谁的
             * sub: The subject of the token，token 主题
             * exp: Expiration Time。 token 过期时间，Unix 时间戳格式
             * iat: Issued At。 token 创建时间， Unix 时间戳格式
             * jti: JWT ID。针对当前 token 的唯一标识
             */
            private final String iss;
            private final String sub;
            private final LocalDate exp;
            private final LocalDate iat;
            private final String jti;
            private final String userId;

            private PayLoad(String iss, String sub, LocalDate exp, LocalDate iat, String userId) {
                this.iss = iss;
                this.sub = sub;
                this.exp = exp;
                this.iat = iat;
                this.jti = UUID.randomUUID().toString().replace("-", "");
                this.userId = userId;
            }

            public static PayLoad newInstance(String userId, LocalDate exp, LocalDate iat) {
                return new PayLoad(null, null, exp, iat, userId);
            }

        }

    }
}
