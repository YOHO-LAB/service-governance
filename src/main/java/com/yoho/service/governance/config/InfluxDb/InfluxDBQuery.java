package com.yoho.service.governance.config.InfluxDb;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;

public class InfluxDBQuery {

	@Resource(name = "chooseInfluxDB")
	protected Map<String, InfluxDBModel> chooseInfluxDB;

	protected final Logger logger = LoggerFactory.getLogger(getClass());


	protected QueryResult query(String influxDBName, String command, String database) {
		logger.info("InfluxDBQuery command is {} ",command);
		Query query = new Query(command, database);
		try {
			QueryResult queryResult = chooseInfluxDB.get(influxDBName).getInfluxDB().query(query);
			return queryResult;
		} catch (Exception e) {
			logger.error("error:{},  command: {}", e.toString(), command);
			return null;
		}
	}

	protected InfluxDB queryByCloud(String dbName){
		return chooseInfluxDB.get(dbName).getInfluxDB();
	}

	protected void add(String dbName, String database, Point point){
		queryByCloud(dbName).write(database,"default",point);
	}
}
