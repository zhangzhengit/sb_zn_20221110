package com.vo.common;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年12月13日
 *
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext = null;

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		if (ApplicationContextUtil.applicationContext == null) {
			ApplicationContextUtil.applicationContext = applicationContext;
		}
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static <T> T getBean(final Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}

	public static void replaceBean(final String beanName, final Object targetObj) {
		final ConfigurableApplicationContext context = (ConfigurableApplicationContext) getApplicationContext();

		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
//		beanFactory.removeBeanDefinition("servletContainerZONE");
//		beanFactory.removeBeanDefinition("servletContainerLIFE");

		beanFactory.destroySingleton("servletContainer");
		beanFactory.destroySingleton(beanName);

//		beanFactory.destroySingleton("servletContainerZONE");
//		beanFactory.destroySingleton("servletContainerLIFE");

		beanFactory.registerSingleton(beanName, targetObj);


//		servletContainerZONE

//		final Object bean = context.getBean(beanName);
//		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
//		try {
//			// 反射获取Factory中的singletonObjects 将该名称下的bean进行替换
//			singletonObjects.setAccessible(true);
//			final Map<String, Object> map = (Map<String, Object>) singletonObjects.get(beanFactory);
//			map.put(beanName, targetObj);
//		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//			// FIXME 2022年12月13日 上午6:01:33 zhangzhen: 记得处理这里 TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
