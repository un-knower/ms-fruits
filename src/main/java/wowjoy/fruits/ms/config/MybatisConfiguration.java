package wowjoy.fruits.ms.config;

import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Create d by wangz iwen on 2017/8/21.
 */
@Configuration
@MapperScan("wowjoy.fruits.ms.module")
public class MybatisConfiguration {
    private final static String SQLSESSIONFACTORYBEAN_TYPEALIASESPACKAGE = "wowjoy.fruits.ms";
    @Autowired
    private Environment environment;

//    @Bean
    DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(environment.getProperty("jdbc.uri"));
        dataSourceBuilder.username(environment.getProperty("jdbc.username"));
        dataSourceBuilder.password(environment.getProperty("jdbc.password"));
        dataSourceBuilder.driverClassName(environment.getProperty("jdbc.classDriverName"));
        return dataSourceBuilder.build();
    }

    /**
     * 采用hikari连接池
     * @return
     */
    @Bean
    HikariDataSource hikariDataSource(){
        final HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(environment.getProperty("jdbc.uri"));
        hikariDataSource.setUsername(environment.getProperty("jdbc.username"));
        hikariDataSource.setPassword(environment.getProperty("jdbc.password"));
        hikariDataSource.setDriverClassName(environment.getProperty("jdbc.classDriverName"));
        hikariDataSource.setIdleTimeout(60000);
        hikariDataSource.setMaxLifetime(60000);
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
