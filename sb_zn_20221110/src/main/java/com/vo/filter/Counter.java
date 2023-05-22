package com.vo.filter;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

/**
 * QPS计数器
 *
 * @author zhangzhen
 * @date 2022年12月12日
 *
 */
public class Counter {

	ConcurrentMap<Object, Object> map = Maps.newConcurrentMap();
	public static void get(final long second) {

	}

}
