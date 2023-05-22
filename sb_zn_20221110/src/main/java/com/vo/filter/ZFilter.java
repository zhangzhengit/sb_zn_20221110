package com.vo.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;

/**
 * 请求过滤器，有before和after方法，可以按需求自己实现此接口有选择的覆盖这两个方法。
 * 一个请求在进入本应用后，先执行一组过滤器（此接口的子类）的before方法，
 * 然后转发到后端服务器 ，最后执行一组过滤器（此接口的子类）的after方法。
 * 如果有多个过滤器（一组过滤器），则按order方法的返回值从小到大执行
 *
 *
 * @author zhangzhen
 * @date 2020-12-08 13:16:04
 *
 */
public interface ZFilter extends InitializingBean {
	/**
	 * 在转发到后端服务器之前做的事情
	 *
	 * @param request
	 * @param response
	 * @return true表示继续执行后续ZFilter,false则停止执行
	 *
	 */
	default boolean before(final HttpServletRequest request, final HttpServletResponse response) {
		return true;
	}

	/**
	 * 在转发到后端服务器之后做的事情
	 *
	 * @param r
	 * @param request
	 * @param response
	 * @return true表示继续执行后续ZFilter,false则停止执行
	 *
	 */
	default boolean after(final Object r, final HttpServletRequest request,
			final HttpServletResponse response) {
		return true;
	}

	/**
	 * 表示此ZFilter在多个ZFilter之中的执行顺序，从小到大排列，越小越先执行
	 *
	 * @return
	 *
	 */
	public abstract int order();

}
