package com.vo.common;

import java.security.SecureRandom;

/**
 *
 * 随机数
 *
 * @author zhangzhen
 * @date 2022年11月11日
 *
 */
public class ZR {

	private static final SecureRandom RANDOM = new SecureRandom();

	public static int nextInt(final int bound) {
		return RANDOM.nextInt(bound);
	}
}
