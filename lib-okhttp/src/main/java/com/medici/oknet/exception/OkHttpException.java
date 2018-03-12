package com.medici.oknet.exception;

/**********************************************************
 * @文件名称：OkHttpException.java
 * @文件作者：mrmedici
 * @创建时间：2015年8月19日 上午10:05:08
 * @文件描述：自定义异常类,返回code,msg到业务层
 * @修改历史：2015年8月19日创建初始版本
 **********************************************************/
public class OkHttpException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * the server return code
	 */
	private int code;

	/**
	 * the server return error message
	 */
	private Object exception;

	public OkHttpException(int code, Object exception) {
		this.code = code;
		this.exception = exception;
	}

	public int getCode() {
		return code;
	}

	public Object getException() {
		return exception;
	}
}