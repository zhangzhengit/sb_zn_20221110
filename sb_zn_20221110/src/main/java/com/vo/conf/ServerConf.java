package com.vo.conf;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import com.google.common.collect.Lists;
import com.vo.enums.BalanceStrategyEnum;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多服务器配置，配置通过不同的域名来访问不同的后端服务
 *
 * @author zhangzhen
 * @date 2022年12月12日
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "nx.server")
@Validated
public class ServerConf implements InitializingBean {

	@NotEmpty(message = "nx.server.nameNode 不能配置为空")
	private List<ServerNodeConf> nameNode;

	public final ServerNodeConf findByServerName(final String serverName) {
		if (CollUtil.isEmpty(this.getNameNode())) {
			return null;
		}

		for (int i = 0; i < this.getNameNode().size(); i++) {
			final ServerNodeConf n = this.getNameNode().get(i);
			if (n.getName().equals(serverName)) {
				return n;
			}
		}

		return null;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ServerNodeConf {

		public static final ServerNodeConf LOCALHOST = new ServerNodeConf("localhost", Lists.newArrayList("http://localhost/"), "RANDOM", Integer.MAX_VALUE);
		public static final ServerNodeConf LOCALHOST2 = new ServerNodeConf("127.0.0.1", Lists.newArrayList("http://127.0.0.1/"), "RANDOM", Integer.MAX_VALUE);
		// FIXME 2022年12月12日 下午11:30:22 zhanghen: 是否校验token、记录日志、记录接口性能等配置
		// 都放在此，单独对应一个server

		/**
		 * 此后端服务的名称，与域名对应，如：z1.com
		 */
		private String name;

		/**
		 * 完整的URL，如：http://192.168.2.44:8888/
		 */
		private List<String> url;

		/**
		 * 负载均衡策略
		 */
		private String balanceStrategy;

		/**
		 * 针对于server的QPS，小于0表示不限制，等于0表示禁止访问此服务
		 */
		private Integer qps;

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "ServerConf.afterPropertiesSet()");

		// FIXME 2022年12月12日 下午10:54:49 zhanghen: 在此校验配置文件是否正确
		for (int i = 0; i < this.nameNode.size(); i++) {
			final ServerNodeConf n = this.nameNode.get(i);
			if (StrUtil.isEmpty(n.getName())) {
				throw new IllegalArgumentException("nx.server.nameNode[" + i + "].name 不能配置为空");
			}

			if (CollUtil.isEmpty(n.getUrl())) {
				throw new IllegalArgumentException("nx.server.nameNode[" + i + "].url[0] 不能配置为空");
			}

			if (StrUtil.isEmpty(n.getBalanceStrategy())) {
				throw new IllegalArgumentException("nx.server.nameNode[" + i + "].balanceStrategy 不能配置为空");
			}

			final BalanceStrategyEnum e = BalanceStrategyEnum.valueByName(n.getBalanceStrategy());
			if (Objects.isNull(e)) {
				final String canonicalName = BalanceStrategyEnum.class.getCanonicalName();
				throw new IllegalArgumentException(
						"nx.server.nameNode[" + i + "].balanceStrategy 配置错误，支持的选项请看 " + canonicalName);
			}

			if (Objects.isNull(n.getQps())) {
				throw new IllegalArgumentException("nx.server.nameNode[" + i + "].qps 不能配置为空");
			}

		}

	}








}
