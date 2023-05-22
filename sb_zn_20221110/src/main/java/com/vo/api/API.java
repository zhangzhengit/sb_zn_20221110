package com.vo.api;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vo.conf.NXConf;
import com.vo.conf.ServerConf;
import com.vo.conf.ServerConf.ServerNodeConf;
import com.vo.enums.BalanceStrategyEnum;
import com.vo.enums.MethodEnum;
import com.vo.filter.ZF;
import com.vo.http.ZH;

import cn.hutool.core.util.StrUtil;

/**
 *
 * 客户端请求 > 此应用 > 后端应用 > 此应用 > 客户端
 *
 * 与外部交互的接口，接收外部请求转发到后端服务器，然后复制后端服务器的响应返回给外部请求，
 * 后端服务器返回什么，本应用就返回什么给客户端，不做任何处理（如404页面等）
 *
 * @author zhangzhen
 * @date 2022年11月10日
 *
 */
@Controller
public class API {

	private static final String GB2312 = "GB2312";

	@Autowired
	TomcatServletWebServerFactory tomcatServletWebServerFactory;

	@Autowired
	private ServerConf serverConf;
	@Autowired
	private ZH zh;
	@Autowired
	private NXConf nxConf;

	/**
	 * 接收外部请求转发到后端服务器，然后复制后端服务器的响应返回给外部请求
	 *
	 * @param request
	 * @param response
	 *
	 */
	@RequestMapping(value = "/**")
	public void index(final HttpServletRequest request, final HttpServletResponse response) {
		System.out.println(
				java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "API.index()");

		// 访问了ADMIN页面
		final String admintPath = this.nxConf.getAdminPath();
		if (request.getRequestURI().equals(admintPath)
				|| (NXConf.PATH_SEPARATOR + admintPath).equals(request.getRequestURI())) {
			try {
				request.getRequestDispatcher(AdminAPI.NX_ADMIN).forward(request, response);
			} catch (final IOException | ServletException e) {
				e.printStackTrace();
			}
			return;
		}

		final String serverName = request.getServerName();
		System.out.println("serverName = " + serverName);

		final ServerNodeConf serverNodeConf = this.serverConf.findByServerName(serverName);
		if (Objects.isNull(serverNodeConf)) {
			final String message = "ServerNode未配置,serverName = " + serverName;
			API.wirteErrorMessage(response, message);
			return;
		}

		final MethodEnum methodEnum = MethodEnum.valueByName(request.getMethod());
		if (Objects.isNull(methodEnum)) {
			final String message = "NX不支持的method = " + request.getMethod();
			API.wirteErrorMessage(response, message);
			return;
		}

		ZF.before(request, response);

		final String url = API.url(request, serverNodeConf);

		response.setHeader("Server", "NX");
		this.request(request, response, methodEnum, url);

		ZF.after(request, response);

	}

	private static String url(final HttpServletRequest request,final ServerNodeConf serverNodeConf) {

		if (serverNodeConf.getUrl().size() == 1) {
			return serverNodeConf.getUrl().get(0);
		}

		final BalanceStrategyEnum balanceStrategyEnum = BalanceStrategyEnum.valueByName(serverNodeConf.getBalanceStrategy());

		final String u = balanceStrategyEnum.g(request, serverNodeConf.getUrl());
		if (NXConf.PATH_SEPARATOR.endsWith(request.getRequestURI())) {
			return u;
		}

		final String x = u + NXConf.PATH_SEPARATOR + request.getRequestURI();
		return x;
	}

	/**
	 * 转发到后端服务器，并返回结果
	 *
	 * @param request
	 * @param response
	 * @param methodEnum
	 * @param urlParam
	 *
	 */
	private void request(final HttpServletRequest request, final HttpServletResponse response, final MethodEnum methodEnum, final String urlParam) {

		final String queryString = request.getQueryString();
		final String url = StrUtil.isEmpty(queryString) ? urlParam : urlParam + "?" + queryString;

		switch (methodEnum) {

		case GET:
			this.zh.get(url, request, response);
			break;

		case POST:
			this.zh.post(url, request, response);
			break;

		default:
			break;
		}
	}

	private static void wirteErrorMessage(final HttpServletResponse response, final String message) {
		try {
			// FIXME 2022年11月11日 下午8:30:49 zhanghen: 提示具体信息 配置
			final ServletOutputStream outputStream = response.getOutputStream();
			outputStream.write(message.getBytes(GB2312));
			outputStream.flush();
			outputStream.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
