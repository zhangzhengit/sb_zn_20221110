package com.vo.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import com.google.common.collect.Lists;
import com.vo.core.ZLog2;
import com.vo.enums.BalanceStrategyEnum;
import com.vo.enums.MethodEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * nx相关的配置
 *
 * @author zhangzhen
 * @date 2022年11月10日
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "nx")
@Validated
public class NXConf implements InitializingBean {

	public static final String PATH_SEPARATOR = "/";
	private static final ZLog2 LOG = ZLog2.getInstance();

	/**
	 * 配置ZFilter是否启用 <ZFilter名称，true/false>
	 */
	@NotEmpty(message = "fileterEnable 不能为空")
	private Map<String, Boolean> fileterEnable;

	/**
	 * admin接口
	 */
	@NotEmpty(message = "adminPath 不能为空")
	// FIXME 2022年12月12日 上午1:53:05 zhanghen: 应用启动时发送一个http请求到nxAdmin配置的路径，测试此接口
	// 如果已存在则提示重新配置此项
	private String adminPath = "nxAdmin";

	/**
	 * 配置的属性值必须与 @see BalanceStrategyEnum.name()保持一致
	 */
	@NotEmpty(message = "balanceStrategy不能为空")
	private String balanceStrategy;

//	public String url(final HttpServletRequest request) {
//		final BalanceStrategyEnum balanceStrategyEnum = this.balanceStrategyEnumReference.get();
//		final String u = balanceStrategyEnum.g(request, this.getUrl());
//		final String x = u + PATH_SEPARATOR + request.getRequestURI();
//		return x;
//	}

	public Boolean isFilterEnable(final String filterName) {
		final Map<String, Boolean> map = this.getFileterEnable();
		final Boolean enable = map.get(filterName);
		if (Objects.isNull(enable)) {
			return false;
		}
		return enable;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "NXConf.afterPropertiesSet()");

		final ArrayList<BalanceStrategyEnum> list = Lists.newArrayList(BalanceStrategyEnum.values());
		final Optional<BalanceStrategyEnum> balanceStrategyEnumOptional = list.stream()
						.filter(e -> e.name().equals(this.getBalanceStrategy()))
						.findAny();
		if (!balanceStrategyEnumOptional.isPresent()) {
			throw new IllegalArgumentException(" balanceStrategy配置的属性值必须与 BalanceStrategyEnum.name()保持一致");
		}

		LOG.info("nx配置balanceStrategy={}", balanceStrategyEnumOptional.get());
		this.balanceStrategyEnumReference.set(balanceStrategyEnumOptional.get());
	}

	/**
	 * 请求接口404，返回给外部接口的信息
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Configuration
	@ConfigurationProperties(prefix = "nx.status-code-not-found")
	@Validated
	public static class NotFoundConf {

		private final int code = 404;

		@NotEmpty(message = "NotFound-message提示语不能为空")
		private String message = "请求地址NotFound";
	}

	/**
	 * 非200时的提示信息，返回给外部接口的信息
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Configuration
	@ConfigurationProperties(prefix = "nx.status-code-error")
	@Validated
	public static class ErrorConf {

		@NotEmpty(message = "error-message提示语不能为空")
		private String message = "错误";
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Configuration
	@ConfigurationProperties(prefix = "nx.token")
	@Validated
	public static class TokenConf {

		@NotEmpty(message = "nx.token.keyword 不能配置为空")
		private String keyword = "token";

		@NotEmpty(message = "nx.token.charsetName 不能配置为空")
		private String charsetName = "GB2312";

		/**
		 * 配置的排除的接口列表
		 */
		private List<String> exclude;
		/**
		 * 	配置排除的后缀名，如配置【.css】则所有.css文件不校验token
		 */
		private List<String> excludeSuffix;

		/**
		 * 配置的排除的接口列表匹配模式,正则表达式
		 */
		private List<String> excludePattern;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Configuration
	@ConfigurationProperties(prefix = "nx.qpslimit")
	@Validated
	public static class QPSLimitConf {

		public static final int NOT_ALLOW = 0;

//		@NotEmpty(message = "nx.qpslimit.keyword 不能配置为空")
//		private String keyword = "token";

		/**
		 * <接口路径,QPS>
		 * 	小于0表示不限制;
		 *  等于0表示不允许访问此接口
		 */
		private Map<String, Integer> conf;

	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Configuration
	@ConfigurationProperties(prefix = "nx.cache")
	@Validated
	public static class CacheConf implements InitializingBean {

		private final String method = MethodEnum.GET.name();

		@NotNull(message = "cache-过期时间second不能为空")
		private Integer second = 1;

		@Override
		public void afterPropertiesSet() throws Exception {
			final Integer second2 = this.getSecond();
			if (second2.intValue() <= 0) {
				throw new IllegalArgumentException("ache-过期时间second 必须大于0");
			}
		}

	}

	private final AtomicReference<BalanceStrategyEnum> balanceStrategyEnumReference = new AtomicReference<>();
}
