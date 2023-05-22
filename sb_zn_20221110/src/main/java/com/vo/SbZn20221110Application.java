package com.vo;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.vo.conf.NXSSLConf;

/**
 * 模拟nginx功能
 *
 * 注意！！！！ http请求通过HttpURLConnection实现的，
 * 带有请求体的get会自动转为post，所以本应用暂不支持带有请求体的get请求!!!
 *
 *
 * @author zhangzhen
 * @date 2022年11月10日
 *
 */
@SpringBootApplication
// FIXME 2022年12月13日 上午6:43:51 zhanghen: 貌似给每个域名配置单独证书行不通，只可以支持多域名证书
public class SbZn20221110Application {

	public static ConfigurableApplicationContext context = null;

	public static void main(final String[] args) {
		context = SpringApplication.run(SbZn20221110Application.class, args);
		System.out.println("MAIN");
	}


	@Autowired
	private NXSSLConf nxsslConf;

	public static Object getBean(final Class cls) {
		final Object bean = context.getBean(cls);
		return bean;
	}

	@Bean
	public TomcatServletWebServerFactory servletContainer() {
		System.out.println("servletContainer nxsslConf = " + this.nxsslConf);

		if (!this.nxsslConf.getEnabled()) {
			final TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
			return factory;
		}

		final TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
			@Override
			protected void postProcessContext(final Context context) {
				final SecurityConstraint constraint = new SecurityConstraint();
				constraint.setUserConstraint("CONFIDENTIAL");
				final SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/*");
				constraint.addCollection(collection);
				context.addConstraint(constraint);
			}
		};
		tomcat.addAdditionalTomcatConnectors(this.httpConnector());

		final Ssl ssl = new Ssl();
		ssl.setKeyStore(this.nxsslConf.getKeyStore());
		ssl.setKeyStoreType(this.nxsslConf.getKeyStoreType());
		ssl.setKeyStorePassword(this.nxsslConf.getKeyStorePassword());

		tomcat.setSsl(ssl);

		return tomcat;
	}

	@Bean
	public Connector httpConnector() {
		final Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		// Connector监听的http的端口号
		connector.setPort(this.nxsslConf.getPort());
		connector.setSecure(false);
		// 监听到http的端口号后转向到的https的端口号
		connector.setRedirectPort(this.nxsslConf.getSslPort());
		return connector;
	}

}
