package com.example.demo.utils.dsTools;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@MapperScan(basePackages = "com.example.demo.dao.druid", sqlSessionFactoryRef = "sqlSessionFactory_druid")
public class DruidDSTool {

//    private String driverClassName;
//    private String url;
//    private String username;
//    private String password;
//    private String initialSize;
//    private String maxActive;
//    private String minIdle;
//    private String maxWaitMillis;
//    private String timeBetweenEvictionRunsMillis;
//    private String numTestsPerEvictionRun;
//    private String testWhileIdle;
//    private String testOnBorrow;
//    private String testOnReturn;
//    private String poolPreparedStatements;
//    private String maxPoolPreparedStatementPerConnectionSize;
//    private String validationQuery;

    private static final String DRUID = "spring.jta.atomikos.datasource.druid.self";

    //  private static final String MYBATIS_CONFIG = "mybatis-config.xml";
    private static final String MYBATIS_XML = "classpath:mapper/durid/*.xml";
    private static final String TYPEALIASPACKAGE = "com.example.demo.domain";

    /**
     * 开启atomikos事务管理器
     */
    @Bean(name = "dataSource_druid")
    @ConfigurationProperties(prefix = DRUID)
    public DataSource dataSource() {
        return new AtomikosDataSourceBean();
    }

//    /**
//     * 开启atomikos事务管理器
//     */
//    @Bean(name = "dataSource_druid")
//    public DataSource dataSource() {
//        DruidXADataSource dbs = null;
//        try {
//            dbs = new DruidXADataSource();
//            dbs.setDriverClassName(driverClassName);
//            dbs.setUrl(url);
//            dbs.setUsername(username);
//            dbs.setPassword(password);
//
//            //atomikos事务管理器
//            AtomikosDataSourceBean xaDS = new AtomikosDataSourceBean();
//            xaDS.setBeanName("dataSource_druid");
//            xaDS.setXaDataSource(dbs);
//            xaDS.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
//            xaDS.setPoolSize(20);
//            xaDS.setXaProperties(build());
//        } catch (Exception e) {
//            log.error("---------- Datasource_druid --- 数据源初始化异常 ---------- {}", e);
//        }
//        return dbs;
//    }

//    private Properties build() {
//        Properties props = new Properties();
//        props.put("initialSize", Integer.parseInt(initialSize));
//        props.put("maxActive", Integer.parseInt(maxActive));
//        props.put("minIdle", Integer.parseInt(minIdle));
//        props.put("maxWait", Integer.parseInt(maxWaitMillis));
//        props.put("poolPreparedStatements", Boolean.valueOf(poolPreparedStatements));
//        props.put("maxPoolPreparedStatementPerConnectionSize", Integer.parseInt(maxPoolPreparedStatementPerConnectionSize));
//        props.put("timeBetweenEvictionRunsMillis", Integer.parseInt(timeBetweenEvictionRunsMillis));
//        props.put("numTestsPerEvictionRun", Integer.parseInt(numTestsPerEvictionRun));
//        props.put("testWhileIdle", Boolean.valueOf(testWhileIdle));
//        props.put("testOnBorrow", Boolean.valueOf(testOnBorrow));
//        props.put("testOnReturn", Boolean.valueOf(testOnReturn));
//        props.put("validationQuery", validationQuery);
//        return props;
//    }

//    @Bean(name = "dataSource_druid")
//    public DataSource dataSource() {
//        DruidDataSource dbs = null;
//        try {
//            dbs = new DruidDataSource();
//            dbs.setDriverClassName(driverClassName);
//            dbs.setUrl(url);
//            dbs.setUsername(username);
//            dbs.setPassword(password);
//            dbs.setInitialSize(Integer.parseInt(initialSize));
//            dbs.setMaxActive(Integer.parseInt(maxActive));
//            dbs.setMinIdle(Integer.parseInt(minIdle));
//            dbs.setMaxWait(Integer.parseInt(maxWaitMillis));
//            dbs.setTimeBetweenEvictionRunsMillis(Integer.parseInt(timeBetweenEvictionRunsMillis));
//            dbs.setMinEvictableIdleTimeMillis(Integer.parseInt(numTestsPerEvictionRun));
//            dbs.setTestWhileIdle(Boolean.valueOf(testWhileIdle));
//            dbs.setTestOnBorrow(Boolean.valueOf(testOnBorrow));
//            dbs.setTestOnReturn(Boolean.valueOf(testOnReturn));
//            dbs.setPoolPreparedStatements(Boolean.valueOf(poolPreparedstatements));
//            dbs.setMaxOpenPreparedStatements(Integer.parseInt(maxOpenPreparedstatements));
//            dbs.setValidationQuery(validationQuery);
//        } catch (Exception e) {
//            log.error("---------- Datasource_druid --- 数据源初始化异常 ---------- {}", e);
//        }
//        return dbs;
//    }

    @Bean(name = "sqlSessionFactory_druid")
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("dataSource_druid") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        //设置mybatis configuration 扫描路径
//        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(MYBATIS_CONFIG));

        //设置typeAlias 包扫描路径
        sqlSessionFactoryBean.setTypeAliasesPackage(TYPEALIASPACKAGE);

        //添加XML目录
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MYBATIS_XML));
        return sqlSessionFactoryBean.getObject();
    }

//    @Bean(name = "transactionManager_druid")
//    public DataSourceTransactionManager testTransactionManager(@Qualifier("dataSource_druid") DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }

    @Bean(name = "sqlSessionTemplate_druid")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("sqlSessionFactory_druid") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
