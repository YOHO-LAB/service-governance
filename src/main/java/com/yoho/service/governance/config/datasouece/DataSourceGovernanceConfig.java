package com.yoho.service.governance.config.datasouece;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.yoho.service.governance.mapper", sqlSessionTemplateRef = "governanceSqlSessionTemplate")
public class DataSourceGovernanceConfig extends AbstractDataSourceConfig {
	@Value("${datasource.url}")
	private String dbUrl;

	@Value("${datasource.username}")
	private String username;

	@Value("${datasource.password}")
	private String password;

	@Value("${datasource.driverClassName}")
	private String driverClassName;

	@Value("${datasource.minIdle}")
	private int minIdle;

	@Value("${datasource.maxActive}")
	private int maxActive;

	@Value("${datasource.maxWait}")
	private int maxWait;

	@Value("${datasource.timeBetweenEvictionRunsMillis}")
	private int timeBetweenEvictionRunsMillis;

	@Value("${datasource.minEvictableIdleTimeMillis}")
	private int minEvictableIdleTimeMillis;

	@Value("${datasource.validationQuery}")
	private String validationQuery;

	@Value("${datasource.testWhileIdle}")
	private boolean testWhileIdle;

	@Value("${datasource.testOnBorrow}")
	private boolean testOnBorrow;

	@Value("${datasource.testOnReturn}")
	private boolean testOnReturn;

	@Value("${datasource.poolPreparedStatements}")
	private boolean poolPreparedStatements;

	@Value("${datasource.maxPoolPreparedStatementPerConnectionSize}")
	private int maxPoolPreparedStatementPerConnectionSize;

	@Value("{datasource.connectionProperties}")
	private String connectionProperties;

	@Bean(name = "governanceDataSource")
	public DataSource runDataSource() {
		return getDataSource(dbUrl, username, password, driverClassName, minIdle, maxActive, maxWait,
				timeBetweenEvictionRunsMillis, minEvictableIdleTimeMillis, validationQuery, testWhileIdle,
				testOnBorrow, testOnReturn, poolPreparedStatements, maxPoolPreparedStatementPerConnectionSize, connectionProperties);
	}

	@Bean(name = "governanceSqlSessionFactory")
	public SqlSessionFactory runSqlSessionFactory(@Qualifier("governanceDataSource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/*.xml"));
		return bean.getObject();
	}

	@Bean(name = "governanceTransactionManager")
	public DataSourceTransactionManager runTransactionManager(@Qualifier("governanceDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = "governanceSqlSessionTemplate")
	public SqlSessionTemplate runSqlSessionTemplate(@Qualifier("governanceSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
