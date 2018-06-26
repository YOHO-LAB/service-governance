package com.yoho.service.governance.config.datasouece;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

/**
 * Created by lygmlj on 2017/7/20.
 */
public abstract class AbstractDataSourceConfig {

	public DataSource getDataSource(String dbUrl, String username, String password, String driverClassName, int minIdle, int maxActive, int maxWait, int timeBetweenEvictionRunsMillis, int minEvictableIdleTimeMillis, String validationQuery, boolean testWhileIdle, boolean testOnBorrow, boolean testOnReturn, boolean poolPreparedStatements, int maxPoolPreparedStatementPerConnectionSize, String connectionProperties) {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(dbUrl);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setDriverClassName(driverClassName);
		dataSource.setMinIdle(minIdle);
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxWait(maxWait);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		dataSource.setValidationQuery(validationQuery);
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setTestOnReturn(testOnReturn);
		dataSource.setPoolPreparedStatements(poolPreparedStatements);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
		dataSource.setConnectionProperties(connectionProperties);
		return dataSource;
	}
}
