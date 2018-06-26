package com.yoho.service.governance.config.InfluxDb;

import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration("InfluxDB")
public class InfluxDBConfig {
	@Value("${influxDb.num}")
	private String influxDbNum;
	@Value("${influxDb.name}")
	private String influxDbName;
	@Value("${influxDb.ip}")
	private String influxDbIp;
	@Value("${influxDb.user}")
	private String influxDbUser;
	@Value("${influxDb.pwd}")
	private String influxDbPwd;
	@Value("${influxDb.timeout}")
	private String influxDbConnectTimeOut;
	@Value("${influxDb.read.timeout}")
	private String influxDbReadTimeOut;
	@Value("${influxDb.write.timeout}")
	private String influxDbWriteTimeOut;

	@Bean(name = "chooseInfluxDB")
	Map<String, InfluxDBModel> chooseInfluxDB() {
		HashMap<String, InfluxDBModel> map = new HashMap<>();
		int num = Integer.valueOf(influxDbNum);
		String[] nameArray = influxDbName.split(";");
		String[] ipArray = influxDbIp.split(";");
		String[] userArray = influxDbUser.split(";");
		String[] pwdArray = influxDbPwd.split(";");
		String[] connectTimeOutArray = influxDbConnectTimeOut.split(";");
		String[] readTimeOutArray = influxDbReadTimeOut.split(";");
		String[] writeTimeOutArray = influxDbWriteTimeOut.split(";");


		InfluxDBModel influxDBModel;
		for (int i = 0; i < num; i++) {
			influxDBModel = new InfluxDBModel();
			influxDBModel.setName(nameArray[i]);
			influxDBModel.setIp(ipArray[i]);
			influxDBModel.setUser(userArray[i]);
			influxDBModel.setPwd(pwdArray[i]);

			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.connectTimeout(Integer.valueOf(connectTimeOutArray[i]), TimeUnit.SECONDS);
			builder.readTimeout(Integer.valueOf(readTimeOutArray[i]), TimeUnit.SECONDS);
			builder.writeTimeout(Integer.valueOf(writeTimeOutArray[i]), TimeUnit.SECONDS);

			InfluxDB influxDB = InfluxDBFactory.connect(ipArray[i],
					userArray[i], pwdArray[i], builder);
			influxDB.enableBatch(100, 30, TimeUnit.SECONDS);
			influxDBModel.setInfluxDB(influxDB);
			map.put(nameArray[i], influxDBModel);
		}
		return map;
	}
}