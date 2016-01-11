package com.gifisan.mtp.component;

import com.alibaba.fastjson.JSONObject;
import com.gifisan.mtp.common.StringUtil;

public class DefaultParameters implements RequestParam{

	private JSONObject object = null;
	
	public DefaultParameters(String json){
		if (!StringUtil.isNullOrBlank(json)) {
			object = JSONObject.parseObject(json);
		}
	}
	
	public boolean getBooleanParameter(String key) {
		if (object == null) {
			return false;
		}
		return object.getBooleanValue(key);
	}

	public int getIntegerParameter(String key) {
		return getIntegerParameter(key, 0);
	}

	public int getIntegerParameter(String key, int defaultValue) {
		if (object == null) {
			return defaultValue;
		}
		int value = object.getIntValue(key);
		if (value == 0) {
			return defaultValue;
		}
		return value;
	}

	public long getLongParameter(String key) {
		return getLongParameter(key, 0);
	}

	public long getLongParameter(String key, long defaultValue) {
		if (object == null) {
			return defaultValue;
		}
		long value = object.getLongValue(key);
		if (value == 0) {
			return defaultValue;
		}
		return value;
	}

	public Object getObjectParameter(String key) {
		if (object == null) {
			return null;
		}
		return object.get(key);
	}

	public String getParameter(String key) {
		return getParameter(key, null);
	}

	public String getParameter(String key, String defaultValue) {
		if (object == null) {
			return defaultValue;
		}
		String value = object.getString(key);
		if (StringUtil.isNullOrBlank(value)) {
			return defaultValue;
		}
		return value;
	}

	
	
}
