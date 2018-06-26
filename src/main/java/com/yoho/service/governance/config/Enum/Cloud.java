package com.yoho.service.governance.config.Enum;

public enum Cloud {
	QCLOUD1("qCloud1"),
	QCLOUD2("qCloud2"),
	QCLOUD3("qCloud3");

	private String name;

	Cloud(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}