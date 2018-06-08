package wowjoy.fruits.ms.module.resource;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FruitResource extends AbstractEntity {

    private String originName;

    private String nowName;

    private Long size;

    private String type;

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getNowName() {
        return nowName;
    }

    public void setNowName(String nowName) {
        this.nowName = nowName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class Upload extends FruitResource implements EntityUtils {
        public Upload() {
            setUuid(obtainUUID());
            setNowName(getUuid());
        }

        private String encodeData;  //base64
        private ByteArrayOutputStream outputStream;

        public void setOutputStream(ByteArrayOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        public void base64ToOutputStream() {
            outputStream = new ByteArrayOutputStream();
            try {
                outputStream.write(Base64.getDecoder().decode(encodeData.getBytes(StandardCharsets.UTF_8)));
                this.setSize((long) outputStream.size());
            } catch (IOException e) {
                throw new ServiceException("base64 to stream error：" + e.getMessage());
            } finally {
                this.setEncodeData(null);
            }
        }

        public ByteArrayOutputStream getOutputStream() {
            return outputStream;
        }

        /*小工具：获取描述信息中的Base64图片*/
        public static String obtainImage(String description, Consumer<FruitResource.Upload> uploadConsumer) {
            return Optional.ofNullable(description)
                    .filter(StringUtils::isNotBlank)
                    .map(str -> {
                        Matcher matcherDescription = Pattern.compile("data:(.*?)\">", Pattern.CASE_INSENSITIVE).matcher(str);
                        StringBuffer replace = new StringBuffer();
                        String[] split;
                        FruitResource.Upload upload;
                        while (matcherDescription.find()) {
                            upload = new FruitResource.Upload();
                            split = matcherDescription.group().split(",");
                            upload.setEncodeData(split[1].substring(0, split[1].length() - 2));
                            upload.base64ToOutputStream();
                            upload.setOriginName(split[0]);
                            upload.setType(split[0]);
                            matcherDescription.appendReplacement(replace, MessageFormat.format("/v1/resource/image/{0}\">", upload.getUuid()));
                            uploadConsumer.accept(upload);
                        }
                        matcherDescription.appendTail(replace);
                        return replace.toString();
                    })
                    .orElse(null);

        }

        public String getEncodeData() {
            return encodeData;
        }

        public void setEncodeData(String encodeData) {
            this.encodeData = encodeData;
        }
    }

    public static class Download extends FruitResource {
        public Download() {
            setUuid(null);
        }

        private String mimeType;
        private String encodeData;

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getEncodeData() {
            return encodeData;
        }

        public void setEncodeData(String encodeData) {
            this.encodeData = encodeData;
        }
    }
}