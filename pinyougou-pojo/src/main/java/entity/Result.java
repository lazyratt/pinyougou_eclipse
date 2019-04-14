package entity;

import java.io.Serializable;

public class Result implements Serializable {

	private boolean success;//状态
	private String message;//消息
	
	
	public Result() {
		super();
	}


	public Result(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}


	public boolean isSuccess() {
		return success;
	}


	public void setSuccess(boolean success) {
		this.success = success;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
