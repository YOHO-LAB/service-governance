package com.yoho.service.governance.util;

import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class QueryResultUtil {
	
	private static Logger logger = LoggerFactory.getLogger(QueryResultUtil.class);
	
	/**
	 * 从查询结果中取values
	 * @param queryResult
	 * @return
	 */
	public static List<List<Object>> getValues(QueryResult queryResult) {
				
		try {
			Series series = getSeries(queryResult).get(0);
			List<List<Object>> values = series.getValues();
			return values;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 从查询结果中取Series
	 * @param queryResult
	 * @return
	 */
	public static List<Series> getSeries(QueryResult queryResult) {
		try {   
			List<Series> seriesList = queryResult.getResults().get(0).getSeries();
			return seriesList;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 从QueryResult中取出count字段
	 * @return
	 */
	public static int getCount(QueryResult queryResult) {
		List<Series> series = getSeries(queryResult);
		if (series == null) {
			return 0;
		}
		return (int)(double)series.get(0).getValues().get(0).get(1);
	}

}
