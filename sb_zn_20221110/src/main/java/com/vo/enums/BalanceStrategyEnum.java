package com.vo.enums;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Maps;
import com.vo.common.BSC;
import com.vo.common.IpUtil;
import com.vo.common.ZR;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对于后端服务器多个URL的负载均衡策略
 *
 * @author zhangzhen
 * @date 2022年11月11日
 *
 */
@Getter
@AllArgsConstructor
public enum BalanceStrategyEnum {

	ROUND_ROBIN("轮询") {
		@Override
		public String g(final HttpServletRequest request, final List<String> urlList) {
			if (urlList.size() == 1) {
				return urlList.get(0);
			}

			final int i = BSC.getNext(urlList.size());
			final String url = urlList.get(i);
			return url;
		}
	},

	RANDOM("随机") {
		@Override
		public String g(final HttpServletRequest request, final List<String> urlList) {
			if (urlList.size() == 1) {
				return urlList.get(0);
			}

			return urlList.get(ZR.nextInt(urlList.size()));
		}
	},

	HASH_IP("根据request的IP hash") {
		@Override
		public String g(final HttpServletRequest request, final List<String> urlList) {
			if (urlList.size() == 1) {
				return urlList.get(0);
			}

			final String ip = IpUtil.getIpAddress(request);

			final int i = Math.abs(ip.hashCode()) % urlList.size();
			return urlList.get(i);
		}
	},

	HASH_URI("根据request的URI hash") {
		@Override
		public String g(final HttpServletRequest request, final List<String> urlList) {
			if (urlList.size() == 1) {
				return urlList.get(0);
			}

			final String uri = request.getRequestURI();
			final int i = Math.abs(uri.hashCode()) % urlList.size();
			return urlList.get(i);
		}
	},

	// TODO 写权重
//	WEIGHT("根据配置的权重"),
	;

	private String description;

	/**
	 * 返回此策略下的后端服务器的url
	 *
	 * @param request
	 * @param urlList
	 * @return
	 *
	 */
	public abstract String g(final HttpServletRequest request, List<String> urlList);

	public static BalanceStrategyEnum valueByName(final String name) {
		final BalanceStrategyEnum e = newHashMap.get(name);
		return e;
	}

	static HashMap<String, BalanceStrategyEnum> newHashMap = Maps.newHashMap();
	static {
		final BalanceStrategyEnum[] vvv = values();
		for (final BalanceStrategyEnum e : vvv) {
			newHashMap.put(e.name(), e);
		}
	}

}
