package wowjoy.fruits.ms.dao;

import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by wangziwen on 2017/8/29.
 * 脑子短路写的，如果能用上最好
 */
@Deprecated
public class CGLibProxyDao<T extends InterfaceDao> {
    private T interfaceDao;
    private Enhancer enhancer;

    private CGLibProxyDao() {

    }

    private CGLibProxyDao(T interfaceDao) {
        this.setInterfaceDao(interfaceDao);
        this.bind();

    }

    public static CGLibProxyDao getInstance(Class<? extends InterfaceDao> abstractEntityClass) {
        CGLibProxyDao result = null;
        try {
            result = new CGLibProxyDao(abstractEntityClass.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    public T getInterfaceDao() {
        return interfaceDao;
    }

    public void setInterfaceDao(T interfaceDao) {
        this.interfaceDao = interfaceDao;
    }

    private Enhancer getEnhancer() {
        return enhancer;
    }

    private void setEnhancer(Enhancer enhancer) {
        this.enhancer = enhancer;
    }

    /**********
     * PUBLIC *
     **********/

    /**
     * 获取代理后的对象
     *
     * @return
     */
    public T create() {
        return (T) this.getEnhancer().create();
    }

    /**
     * 加载代理对象
     *
     * @return
     */
    public CGLibProxyDao bind() {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.getInterfaceDao().getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                parameterTypeDate();
                return null;
            }
        });
        return this;
    }

    /**************
     * 代理函数管理 *
     **************/

    public void parameterTypeDate() {
        LoggerFactory.getLogger(this.getClass()).info("处理日期类型数据");
    }

}
