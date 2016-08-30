package com.haogre.pay.wechat.utils;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * @author haoz
 * 
 * @date 2016-8-30
 *
 */
public class JsonResult implements Serializable {

	private static final long serialVersionUID = 6208047634393884852L;

	JSONObject result = new JSONObject();
	JSONObject status = new JSONObject();
	private int code;
	private String msg;
	private JSONObject data;

	public JsonResult() {
		this.setCode(Results.RESULT_CODE_SUCCESS);
		this.setMsg(Results.RESULT_MSG_SUCCESS);
		result.put("status", status);
	}
	public JsonResult(JSONObject data) {
		this.setCode(Results.RESULT_CODE_SUCCESS);
		this.setMsg(Results.RESULT_MSG_SUCCESS);
		result.put("status", status);
		this.setData(data);
	}

	public void setMsg(String msg) {
		this.msg = msg;
		status.put("msg", msg);
	}

	public JSONObject getStatus() {
		return status;
	}

	public void setCode(int code) {
		this.code = code;
		status.put("code", code);
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public String toString() {
		status.put("code", code);
		status.put("msg", msg);
		result.put("data", data);
		return result.toJSONString();
	}

	public enum statusCode {

	}
}
