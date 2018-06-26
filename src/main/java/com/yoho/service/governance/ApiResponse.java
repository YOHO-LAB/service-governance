package com.yoho.service.governance;

import org.springframework.util.StringUtils;


public class ApiResponse {

	public static String DEFAULT_MSG = "操作成功";
	public static int DEFAULT_CODE = 200;

	private int code;
	private String message;
	private Object data;

	public ApiResponse() {
		this(200, DEFAULT_MSG, null);
	}

	public ApiResponse(Object data) {
		this();
		this.data = data;
	}

	public ApiResponse(int code, String message) {
		this(code, message, null);
	}

	public ApiResponse(int code, String message, Object data) {
		this.code = code;
		if (!StringUtils.isEmpty(message)) {
			this.message = message;
		}
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public ApiResponse setCode(int code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public ApiResponse setMessage(String message) {
		this.message = message;
		return this;
	}

	public Object getData() {
		return data;
	}

	public ApiResponse setData(Object data) {
		this.data = data;
		return this;
	}


	/**
	 * 构造响应。 使用方式：
	 * <p/>
	 * <pre>
	 *  ApiResponse.ApiResponseBuilder builder = new ApiResponse.ApiResponseBuilder();
	 *  ApiResponse apiResponse =  builder.code(200).message("coupons total").data(new Total("0")).build();
	 * </pre>
	 */
	public static class ApiResponseBuilder {
		ApiResponse apiResponse;

		public ApiResponseBuilder() {
			apiResponse = new ApiResponse();
		}

		/**
		 * 设置错误码。默认200
		 *
		 * @param code 错误码
		 * @return ApiResponseBuilder
		 */
		public ApiResponseBuilder code(int code) {
			apiResponse.code = code;
			return this;
		}

		/**
		 * 设置消息。默认[操作成功]
		 *
		 * @param message 错误消息
		 * @return ApiResponseBuilder
		 */
		public ApiResponseBuilder message(String message) {
			apiResponse.message = message;
			return this;
		}
		/**
		 * 设置响应的具体内容
		 *
		 * @param data 响应的具体内容
		 * @return 内容
		 */
		public ApiResponseBuilder data(Object data) {
			apiResponse.data = data;
			return this;
		}

		/**
		 * 构造响应
		 *
		 * @return 响应
		 */
		public ApiResponse build() {
			//参数校验, 并且设置默认值
			if (this.apiResponse.code <= 0) {
				this.apiResponse.code = DEFAULT_CODE;
			}
			if (StringUtils.isEmpty(apiResponse.message)) {
				this.apiResponse.message = DEFAULT_MSG;
			}

			return apiResponse;
		}

	}
}
