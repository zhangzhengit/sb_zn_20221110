package com.vo.filter;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年11月15日
 *
 */
public class T {

	private final static ThreadLocal<String> t = new ThreadLocal<>();

	public static void set(final String v) {
		t.set(v);
	}

	public static String get() {
		final String v = t.get();
		return v;
	}
}
