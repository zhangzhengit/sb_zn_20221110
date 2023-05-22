package com.vo.filter;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vo.common.IpUtil;
import com.vo.conf.NXConf;
import com.vo.core.ZLog2;

/**
 * 记录日志，order=0，最先执行。具体记录的内容去重写此的after和before方法
 *
 * @author zhangzhen
 * @date 2022年11月11日
 *
 */
@Component
public class LogFilter implements ZFilter {

	private static final ZLog2 LOG = ZLog2.getInstance();

	@Autowired
	private NXConf nxConf;

	@Override
	public boolean before(final HttpServletRequest request, final HttpServletResponse response) {
		final String requestId = UUID.randomUUID().toString();
		T.set(requestId);

		final String ip = IpUtil.getIpAddress(request);
		final String requestURI = request.getRequestURI();
		final String queryString = request.getQueryString();

		LOG.info("[请求]requestId={},clientIp={},method={},requestURI={},queryString=[{}]",
				requestId,
				ip,
				request.getMethod(),
				requestURI,
				queryString);

		return ZFilter.super.before(request, response);
	}

	@Override
	public boolean after(final Object r, final HttpServletRequest request, final HttpServletResponse response) {

		final String ip = IpUtil.getIpAddress(request);
		final String requestURI = request.getRequestURI();
		final String contentType = response.getContentType();

		final int status = response.getStatus();

		final String requestId = T.get();

		LOG.info("[响应]requestId={},clientIp={},method={},requestURI={},responseStatus={},responseContentType={}",
				requestId,
				ip,
				request.getMethod(),
				requestURI,
				status,
				contentType

				);
		return ZFilter.super.after(r, request, response);
	}

	/**
	 * order 设为最小，最先记录日志
	 */
	@Override
	public int order() {
		return Integer.MIN_VALUE + 1;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.nxConf.isFilterEnable(this.getClass().getSimpleName())) {
			ZF.add(this);
		}
	}

}
