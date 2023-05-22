package com.vo.filter;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vo.conf.NXConf;
import com.vo.conf.NXConf.QPSLimitConf;
import com.vo.core.ZLog2;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年12月12日
 *
 */
@Component
public class QPSLimitFilter implements ZFilter {

	private static final ZLog2 LOG = ZLog2.getInstance();

	private final Queue<Integer> queue = new SpecifiedCapacityQueue<>(60);

	@Autowired
	private QPSLimitConf qpsLimitConf;
	@Autowired
	private NXConf nxConf;

	@Override
	public boolean before(final HttpServletRequest request, final HttpServletResponse response) {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "QPSLimitFilter.before()");

		// FIXME 2022年12月12日 上午2:50:11 zhanghen: 在此限流
		final Map<String, Integer> map = this.qpsLimitConf.getConf();
		final Integer qps = map.get(request.getRequestURI());
		if (Objects.isNull(qps) || qps.intValue() < 0) {
			return true;
		}

		if (qps.intValue() == QPSLimitConf.NOT_ALLOW) {
			return false;
		}

		final long ms = System.currentTimeMillis();
		final long second = ms / 1000;

		// FIXME 2022年12月12日 上午3:11:21 zhanghen: 想这里怎么写


		return ZFilter.super.before(request, response);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.nxConf.isFilterEnable(this.getClass().getSimpleName())) {
			System.out.println("QPSLimitFilter 启用");

			ZF.add(this);
		}
	}

	@Override
	public int order() {
		return Integer.MIN_VALUE + 2;
	}

}
