package com.vo.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * https://www.cnblogs.com/CoderGeshu/p/15395136.html
 *
 * @author zhangzhen
 * @date 2022年11月11日
 *
 */
public class IpUtil {

	private static final String UNKNOWN = "unknown";
	private static final String LOCALHOST_IP = "127.0.0.1";
	// 客户端与服务器同为一台机器，获取的 ip 有时候是 ipv6 格式
	private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
	private static final String SEPARATOR = ",";

	// 根据 HttpServletRequest 获取 IP
	public static String getIpAddress(final HttpServletRequest request) {
		if (request == null) {
			return "unknown";
		}
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if (LOCALHOST_IP.equalsIgnoreCase(ip) || LOCALHOST_IPV6.equalsIgnoreCase(ip)) {
				// 根据网卡取本机配置的 IP
				InetAddress iNet = null;
				try {
					iNet = InetAddress.getLocalHost();
				} catch (final UnknownHostException e) {
					e.printStackTrace();
				}
				if (iNet != null) {
					ip = iNet.getHostAddress();
				}
			}
		}
		// 对于通过多个代理的情况，分割出第一个 IP
		if ((ip != null && ip.length() > 15) && (ip.indexOf(SEPARATOR) > 0)) {
			ip = ip.substring(0, ip.indexOf(SEPARATOR));
		}
		return LOCALHOST_IPV6.equals(ip) ? LOCALHOST_IP : ip;
	}
}
