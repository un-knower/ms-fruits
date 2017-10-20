package wowjoy.fruits.ms.util;

import com.google.common.base.Charsets;
import com.google.gson.*;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.HmacUtils;
import wowjoy.fruits.ms.exception.CheckException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

/**
 * Created by wangziwen on 2017/10/19.
 */
public class JwtUtils {
    public static final String salt = "wangziwen";

    public static String token(Jwt.Header header, Jwt.PayLoad payLoad) {
        return Jwt.newHmacSHA256(header, payLoad).toString();
    }

    public static Jwt.Header newHeader() {
        return Jwt.Header.newInstance();
    }

    public static Jwt.PayLoad newPayLoad(String userId, LocalDateTime exp, LocalDateTime iat) {
        return Jwt.PayLoad.newInstance(userId, exp, iat);
    }

    public static Jwt newJwt(String jwt) {
        String[] token = jwt.split("\\.");
        if (token.length < 3)
            throw new CheckException("Jwt token format error");
        Jwt.Header header = Jwt.Header.newInstance(new String(Base64.getDecoder().decode(token[0]), Charsets.UTF_8));
        Jwt.PayLoad payLoad = Jwt.PayLoad.newInstance(new String(Base64.getDecoder().decode(token[1]), Charsets.UTF_8));
        Jwt result = Jwt.newHmacSHA256(header, payLoad);
        if (!result.checkSignature(jwt))
            throw new CheckException("Jwt token Be tampered with");
        return result;
    }

    /**
     * 时间转换字段工具
     */
    public static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        public static LocalDateTimeAdapter getInstance() {
            return new LocalDateTimeAdapter();
        }
    }

    public static class Jwt {
        private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").registerTypeAdapter(LocalDateTime.class, LocalDateTimeAdapter.getInstance()).create();
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

        public boolean checkSignature(String jwt) {
            String[] split = jwt.split("\\.");
            if (split.length < 1)
                throw new CheckException("Token认证失败");
            return signature.equals(split[2]);
        }

        String encryptHmacSHA256() {
            return HmacUtils.hmacSha256Hex(salt, getHeaderBase64() + "." + getPayloadBase64());
        }

        private String getHeaderBase64() {
            return StringUtils.newStringUtf8(Base64.getUrlEncoder().encode(gson.toJsonTree(header).toString().getBytes()));
        }

        private String getPayloadBase64() {
            return StringUtils.newStringUtf8(Base64.getUrlEncoder().encode(gson.toJsonTree(payload).toString().getBytes()));
        }

        public Header getHeader() {
            return header;
        }

        public PayLoad getPayload() {
            return payload;
        }

        public String toString() {
            return this.getHeaderBase64() + "." + this.getPayloadBase64() + "." + this.signature;
        }

        public static Jwt newHmacSHA256(Header header, PayLoad payLoad) {
            return new Jwt(header, payLoad, JwtUtils.salt);
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

            public static Header newInstance(String header) {
                JsonObject json = new JsonParser().parse(header).getAsJsonObject();
                if (!json.has("typ") || !json.has("alg"))
                    throw new CheckException("Token信息错误");
                return new Header(json.get("typ").getAsString(), json.get("alg").getAsString());
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
            private final LocalDateTime exp;
            private final LocalDateTime iat;
            private final String jti;
            private final String userId;

            private PayLoad(String iss, String sub, LocalDateTime exp, LocalDateTime iat, String userId) {
                this(iss, sub, exp, iat, userId, UUID.randomUUID().toString().replace("-", ""));
            }

            private PayLoad(String iss, String sub, LocalDateTime exp, LocalDateTime iat, String userId, String jit) {
                this.iss = iss;
                this.sub = sub;
                this.exp = exp;
                this.iat = iat;
                this.jti = jit;
                this.userId = userId;
            }

            public String getUserId() {
                return userId;
            }

            public static PayLoad newInstance(String userId, LocalDateTime exp, LocalDateTime iat) {
                return new PayLoad(null, null, exp, iat, userId);
            }

            public static PayLoad newInstance(String jti, String userId, LocalDateTime exp, LocalDateTime iat) {
                return new PayLoad(null, null, exp, iat, userId, jti);
            }

            public static PayLoad newInstance(String payload) {
                JsonObject json = new JsonParser().parse(payload).getAsJsonObject();
                if (!json.has("userId") || !json.has("exp") || !json.has("iat"))
                    throw new CheckException("Token信息错误");
                return newInstance(json.get("jti").getAsString(), json.get("userId").getAsString(), LocalDateTime.parse(json.get("exp").getAsString()), LocalDateTime.parse(json.get("iat").getAsString()));
            }
        }

    }

}
