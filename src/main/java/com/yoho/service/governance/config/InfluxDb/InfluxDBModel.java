package com.yoho.service.governance.config.InfluxDb;

import org.influxdb.InfluxDB;

public class InfluxDBModel {

	private String name;

	private String ip;

	private String user;

	private String pwd;

	private InfluxDB influxDB;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public InfluxDB getInfluxDB() {
		return influxDB;
	}

	public void setInfluxDB(InfluxDB influxDB) {
		this.influxDB = influxDB;
	}

}
