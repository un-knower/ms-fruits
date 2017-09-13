package wowjoy.fruits.ms.module;

import java.text.MessageFormat;

/**
 * Created by wangziwen on 2017/9/12.
 */
public class ModuleException extends RuntimeException {
    public ModuleException(String message) {
        super(MessageFormat.format("module error:{0}", message));
    }
}
