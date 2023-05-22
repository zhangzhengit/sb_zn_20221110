package com.vo.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年11月11日
 *
 */
public class BSC {

	private static final int INITIAL_VALUE = -1;
	private static final AtomicInteger CURRENT = new AtomicInteger(INITIAL_VALUE);

	public static int getNext(final int max) {
		if (CURRENT.get() == INITIAL_VALUE) {
			CURRENT.set(0);
		}

		final int n = CURRENT.incrementAndGet();
		if (n >= max) {
			CURRENT.set(0);
		}

		return CURRENT.get();
	}

}
