package com.vo.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vo.common.CR;
import com.vo.conf.NXConf;
import com.vo.conf.NXConf.TokenConf;
import com.vo.core.ZLog2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年12月12日
 *
 */
@Component
public class TokenFilter implements ZFilter {


	private static final ZLog2 LOG = ZLog2.getInstance();

	private final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private TokenConf tokenConf;
	@Autowired
	private NXConf nxConf;

	@Override
	public boolean before(final HttpServletRequest request, final HttpServletResponse response) {

		final List<String> exclude = this.tokenConf.getExclude();
		final String requestURI = request.getRequestURI();

		final List<String> excludeSuffix = this.tokenConf.getExcludeSuffix();
		if (CollUtil.isNotEmpty(excludeSuffix)) {
			for (final String s : excludeSuffix) {
				if (requestURI.endsWith(s)) {
					System.out.println("token 通过 s = " + s + "\t" + "requestURI = " + requestURI);
					return true;
				}
			}
		}

		// 排除的为空(全部校验) 或者 请求URI不在排除接口列表内
		if (CollUtil.isEmpty(exclude) || !exclude.contains(requestURI)) {

			final String token = request.getHeader(this.tokenConf.getKeyword());

			if (StrUtil.isEmpty(token)) {

				try {
					final ServletOutputStream outputStream = response.getOutputStream();

					final CR<Object> error = CR.error("token[" + this.tokenConf.getKeyword() + "]不存在");

					final String json = this.mapper.writeValueAsString(error);
					outputStream.write(json.getBytes(this.tokenConf.getCharsetName()));

					outputStream.flush();
					outputStream.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}

				return false;
			}


			// FIXME 2022年12月12日 上午2:38:08 zhanghen: 继续写，什么？


		}


		return ZFilter.super.before(request, response);
	}

	@Override
	public int order() {
		return Integer.MIN_VALUE;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.nxConf.isFilterEnable(this.getClass().getSimpleName())) {
			ZF.add(this);
		}
	}

}
