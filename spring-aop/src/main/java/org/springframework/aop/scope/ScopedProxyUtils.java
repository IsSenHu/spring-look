/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.scope;

import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Utility class for creating a scoped proxy.
 *
 * <p>Used by ScopedProxyBeanDefinitionDecorator and ClassPathBeanDefinitionScanner.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @since 2.5
 */
public abstract class ScopedProxyUtils {

	private static final String TARGET_NAME_PREFIX = "scopedTarget.";

	private static final int TARGET_NAME_PREFIX_LENGTH = TARGET_NAME_PREFIX.length();


	/**
	 * Generate a scoped proxy for the supplied target bean, registering the target
	 * bean with an internal name and setting 'targetBeanName' on the scoped proxy.
	 * @param definition the original bean definition
	 * @param registry the bean definition registry
	 * @param proxyTargetClass whether to create a target class proxy 是否创建目标类代理
	 * @return the scoped proxy definition
	 * @see #getTargetBeanName(String)
	 * @see #getOriginalBeanName(String)
	 */
	public static BeanDefinitionHolder createScopedProxy(BeanDefinitionHolder definition,
			BeanDefinitionRegistry registry, boolean proxyTargetClass) {

		// 原始Bean名称
		String originalBeanName = definition.getBeanName();
		// 目标BD
		BeanDefinition targetDefinition = definition.getBeanDefinition();
		// 目标Bean名称
		String targetBeanName = getTargetBeanName(originalBeanName);

		// Create a scoped proxy definition for the original bean name,
		// 为原始bean名称创建一个有范围的代理定义，
		// "hiding" the target bean in an internal target definition.
		// 在内部目标定义中“隐藏”目标bean。我们这里使用的是ScopedProxyFactoryBean来产生代理类
		RootBeanDefinition proxyDefinition = new RootBeanDefinition(ScopedProxyFactoryBean.class);
		// 设置装饰定义
		proxyDefinition.setDecoratedDefinition(new BeanDefinitionHolder(targetDefinition, targetBeanName));
		// 设置原始BD
		proxyDefinition.setOriginatingBeanDefinition(targetDefinition);
		// 设置来源
		proxyDefinition.setSource(definition.getSource());
		// 设置角色
		proxyDefinition.setRole(targetDefinition.getRole());
		// 设置ScopedProxyFactoryBean的targetBeanName属性
		proxyDefinition.getPropertyValues().add("targetBeanName", targetBeanName);
		// 如果要代理目标类
		if (proxyTargetClass) {
			// 设置目标BD保留目标类的属性
			targetDefinition.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
			// ScopedProxyFactoryBean's "proxyTargetClass" default is TRUE, so we don't need to set it explicitly here.
			// ScopedProxyFactoryBean的“proxyTargetClass”默认值为TRUE，因此我们无需在此处显式设置它。
		}
		else {
			// 设置ScopedProxyFactoryBean的proxyTargetClass属性为false
			proxyDefinition.getPropertyValues().add("proxyTargetClass", Boolean.FALSE);
		}

		// Copy autowire settings from original bean definition.
		// 拷贝原始BD的自动注入设置
		proxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
		proxyDefinition.setPrimary(targetDefinition.isPrimary());
		if (targetDefinition instanceof AbstractBeanDefinition) {
			proxyDefinition.copyQualifiersFrom((AbstractBeanDefinition) targetDefinition);
		}

		// The target bean should be ignored in favor of the scoped proxy.
		// 应该忽略目标bean，以支持范围代理。
		targetDefinition.setAutowireCandidate(false);
		targetDefinition.setPrimary(false);

		// Register the target bean as separate bean in the factory.
		// 注册目标BD
		registry.registerBeanDefinition(targetBeanName, targetDefinition);

		// Return the scoped proxy definition as primary bean definition
		// (potentially an inner bean).
		// 返回作用域代理定义作为主bean定义（可能是内部bean）。
		return new BeanDefinitionHolder(proxyDefinition, originalBeanName, definition.getAliases());
	}

	/**
	 * Generate the bean name that is used within the scoped proxy to reference the target bean.
	 * @param originalBeanName the original name of bean
	 * @return the generated bean to be used to reference the target bean
	 * @see #getOriginalBeanName(String)
	 */
	public static String getTargetBeanName(String originalBeanName) {
		return TARGET_NAME_PREFIX + originalBeanName;
	}

	/**
	 * Get the original bean name for the provided {@linkplain #getTargetBeanName
	 * target bean name}.
	 * @param targetBeanName the target bean name for the scoped proxy
	 * @return the original bean name
	 * @throws IllegalArgumentException if the supplied bean name does not refer
	 * to the target of a scoped proxy
	 * @since 5.1.10
	 * @see #getTargetBeanName(String)
	 * @see #isScopedTarget(String)
	 */
	public static String getOriginalBeanName(@Nullable String targetBeanName) {
		Assert.isTrue(isScopedTarget(targetBeanName), () -> "bean name '" +
				targetBeanName + "' does not refer to the target of a scoped proxy");
		return targetBeanName.substring(TARGET_NAME_PREFIX_LENGTH);
	}

	/**
	 * Determine if the {@code beanName} is the name of a bean that references
	 * the target bean within a scoped proxy.
	 * @since 4.1.4
	 */
	public static boolean isScopedTarget(@Nullable String beanName) {
		return (beanName != null && beanName.startsWith(TARGET_NAME_PREFIX));
	}

}
