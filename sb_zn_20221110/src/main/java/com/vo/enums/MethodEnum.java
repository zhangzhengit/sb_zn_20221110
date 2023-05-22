package com.vo.enums;

import java.util.HashMap;

import com.google.common.collect.Maps;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年11月11日
 *
 */
public enum MethodEnum {

	GET,

	POST,

	DELTE,

	PUT,;

	public static MethodEnum valueByName(final String name) {
		final MethodEnum e = newHashMap.get(name);
		return e;
	}

	static HashMap<String, MethodEnum> newHashMap = Maps.newHashMap();
	static {
		final MethodEnum[] vvv = values();
		for (final MethodEnum e : vvv) {
			newHashMap.put(e.name(), e);
		}
	}
}
