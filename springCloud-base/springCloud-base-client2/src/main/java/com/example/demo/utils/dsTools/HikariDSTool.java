package com.example.demo.utils.dsTools;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 不支持JTA（多数据源的事务处理）
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource.hikari.self")
@MapperScan(basePackages = "com.example.demo.dao.hikari", sqlSessionFactoryRef = "sqlSessionFactory_hikari")
public class HikariDSTool {

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String maxActive;
    private String minIdle;
    private String maxWaitMillis;
    private String validationQuery;
    private String cachePrepStmts;
    private String prepStmtCacheSize;
    private String prepStmtCacheSqlLimit;
    private String rewriteBatchedStatements;

    //  private static final String MYBATIS_CONFIG = "mybatis-config.xml";
    private static final String MYBATIS_XML = "classpath:mapper/hikari/*.xml";
    private static final String TYPEALIASPACKAGE = "com.example.demo.domain";

    @Bean(name = "dataSource_hikari")
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(Integer.parseInt(maxActive));
        hikariConfig.setMinimumIdle(Integer.parseInt(minIdle));
        hikariConfig.setConnectionTimeout(Integer.parseInt(maxWaitMillis));
        hikariConfig.setConnectionTestQuery(validationQuery);
        hikariConfig.addDataSourceProperty("cachePrepStmts", cachePrepStmts); //是否自定义配置，为true时下面两个参数才生效
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize); //连接池大小默认25，官方推荐250-500
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit); //单条语句最大长度默认256，官方推荐2048
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", rewriteBatchedStatements);
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "sqlSessionFactory_hikari")
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("dataSource_hikari") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        //设置mybatis configuration 扫描路径
//        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(MYBATIS_CONFIG));

        //设置typeAlias 包扫描路径
        sqlSessionFactoryBean.setTypeAliasesPackage(TYPEALIASPACKAGE);

        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(MYBATIS_XML));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "transactionManager_hikari")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("dataSource_hikari") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "sqlSessionTemplate_hikari")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("sqlSessionFactory_hikari") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
