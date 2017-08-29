package wowjoy.fruits.ms.dao;

import org.springframework.context.ApplicationContext;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

/**
 * Created by wangziwen on 2017/8/28.
 */
public interface InterfaceArgument {
    default <T> T findContext(Class<T> tClass) {
        final ApplicationContext context = ApplicationContextUtils.getContext();
        return context.getBean(tClass);
    }

    /**
     * 暂时不处理Date类型
     *
     * @param data
     * @return
     */
    default void convert(Map data) {
        data.forEach((k, y) -> {
            for (Method method : this.getClass().getSuperclass().getDeclaredMethods()) {
                if (method.getName().indexOf("set") == -1) continue;
                if (method.getName().toLowerCase().indexOf(k.toString().toLowerCase()) == -1) continue;
                if (method.getParameterTypes().length != 1) continue;
                if (Date.class.isInstance(method.getParameterTypes()[0].getClass())) continue;
                try {
                    method.invoke(this, y);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
