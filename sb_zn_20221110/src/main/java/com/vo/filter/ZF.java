package com.vo.filter;

import java.util.Comparator;
import java.util.Optional;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 存放多个 ZFilter，并且按order值顺序从小到大执行
 *
 * @author zhangzhen
 * @date 2022年11月11日
 *
 */
public class ZF {

	private static final Vector<ZFilter> vector = new Vector<>();

	public synchronized static void add(final ZFilter zFilter) {

		final Optional<ZFilter> findAny = vector.stream().filter(f -> f.order() == zFilter.order()).findAny();
		if (findAny.isPresent()) {
			final String canonicalName = findAny.get().getClass().getCanonicalName();
			final String canonicalName2 = zFilter.getClass().getCanonicalName();
			throw new IllegalArgumentException("[" + canonicalName + "]和[" + canonicalName2 + "] order 重复,order不可重复！");
		}

		vector.add(zFilter);
		vector.sort(Comparator.comparing(ZFilter::order));
	}

	public static void before(final HttpServletRequest request, final HttpServletResponse response) {
		for (final ZFilter f : vector) {
			final boolean c = f.before(request, response);
			if (!c) {
				break;
			}
		}
	}

	public static void after(final HttpServletRequest request, final HttpServletResponse response) {
		for (final ZFilter f : vector) {
			final boolean c = f.after(null, request, response);
			if (!c) {
				break;
			}
		}
	}
}
