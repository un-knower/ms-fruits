package wowjoy.fruits.ms.config;

import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Create d by wangz iwen on 2017/8/21.
 */
@Configuration
@MapperScan("wowjoy.fruits.ms.module")
@EnableTransactionManagement
public class MybatisConfiguration {
    private final static String SQLSESSIONFACTORYBEAN_TYPEALIASESPACKAGE = "wowjoy.fruits.ms";
    @Autowired
    private Environment environment;

    /**
     * 采用hikari连接池
     *
     * @return
     */
    @Bean
    HikariDataSource hikariDataSource() {
        final HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(environment.getProperty("tomcat.jdbc.pool.uri"));
        hikariDataSource.setUsername(environment.getProperty("tomcat.jdbc.pool.username"));
        hikariDataSource.setPassword(environment.getProperty("tomcat.jdbc.pool.password"));
        hikariDataSource.setDriverClassName(environment.getProperty("tomcat.jdbc.pool.driverClassName"));
        hikariDataSource.setIdleTimeout(60000);
        hikariDataSource.setMaxLifetime(1800000);
        return hikariDataSource;
    }


    @Bean
    SqlSessionFactoryBean sessionFactoryBean() {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(hikariDataSource());
        factoryBean.setTypeAliasesPackage(SQLSESSIONFACTORYBEAN_TYPEALIASESPACKAGE);
        return factoryBean;
    }

    @Bean
    DataSourceTransactionManager dataSourceTransactionManager() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(hikariDataSource());
        return dataSourceTransactionManager;
    }

}
