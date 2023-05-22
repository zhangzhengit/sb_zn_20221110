package com.vo.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NX 给配置的域名的ssl证书，支持多域名的证书
 *
 *
 * @author zhangzhen
 * @date 2022年12月13日
 *
 */
// FIXME 2022年12月13日 下午7:00:36 zhanghen: 给配置的每个域名aa.com 、bb.com 、cc.com
// 各单独配置ssl证书，没想到怎么切换
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "nx.ssl")
@Validated
public class NXSSLConf {

	/**
	 * 是否给配置的域名启用 SSL
	 */
	private Boolean enabled = false;

	private Integer port = 80;

	private Integer sslPort = 443;

	/**
	 * 如：pfx文件放在 resources 目录下，则 classpath:aa.com.pfx
	 *
	 */
	private String keyStore;
	private String keyStorePassword;
	private String keyStoreType;


}
