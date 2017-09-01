package wowjoy.fruits.ms.dao;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

/**
 * Created by wangziwen on 2017/8/28.
 */
@Deprecated
public abstract class InterfaceArgument<T extends InterfaceDao> {
    private InterfaceDao interfaceDao;

    /**
     * 设置数据源
     *
     * @param tClass
     * @return
     */
    public InterfaceArgument<T> setInterfaceDao(Class<T> tClass) {
        this.interfaceDao = ApplicationContextUtils.getContext().getBean(tClass);
        return this;
    }


    /**
     * 推荐各自的管理类实现自己的getInterfaceDao
     *
     * @return
     */
    protected T getInterfaceDao() {
        if (this.interfaceDao == null)
            throw new RuntimeException("没有选择数据模型");
        return (T) interfaceDao;
    }

    /**
     * 调用实体类的set方法，设置数据
     * 方便使用argument
     *
     * @param data
     * @return
     */
    protected void convert(Map data) {
        if (data != null)
            data.forEach((k, y) -> {
                for (Method method : this.getClass().getSuperclass().getDeclaredMethods()) {
                    if (method.getName().indexOf("set") == -1) continue;
                    if (method.getName().toLowerCase().indexOf(k.toString().toLowerCase()) == -1) continue;
                    try {
                        method.invoke(this, valueHandler(y, method.getParameterTypes()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InterfaceArgumentException e) {
                        continue;
                    }
                }
            });
    }

    private Object valueHandler(Object y, Class<?>[] parameter) {
        if (parameter.length > 1) throw new InterfaceArgumentException();
        if (StringUtils.equals(Date.class.getName(), parameter[0].getName())) throw new InterfaceArgumentException();
        if (StringUtils.equals(LocalDate.class.getName(), parameter[0].getName())) {
            y = LocalDate.parse(String.valueOf(y));
        }
        return y;
    }

}
