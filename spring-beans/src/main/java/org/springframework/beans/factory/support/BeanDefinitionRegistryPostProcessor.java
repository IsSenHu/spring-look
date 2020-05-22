/*
 * Copyright 2002-2010 the original author or authors.
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

package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

/**
 * 	针对于BeanDefinitionRegistry
 *  是BeanFactoryPostProcessor的子类，在BeanFactoryPostProcessor之前执行
 *  源码中先遍历的BeanDefinitionRegistryPostProcessor（有spring提供的，还有自定义的）
 *  自定义指的是new出来放进去的。不是实现了这个接口然后加注解@Component。
 *	自定义的先执行
 *	ConfigurationClassPostProcessor扫描、3种import的扫描、@Bean的扫描、判断配置类是否是一个完全的配置类
 *	这个B做了好多好多的事，是Spring最重要的类之一
 *
 * Extension to the standard {@link BeanFactoryPostProcessor} SPI, allowing for
 * the registration of further bean definitions <i>before</i> regular
 * BeanFactoryPostProcessor detection kicks in. In particular,
 * BeanDefinitionRegistryPostProcessor may register further bean definitions
 * which in turn define BeanFactoryPostProcessor instances.
 *
 * @author Juergen Hoeller
 * @since 3.0.1
 * @see org.springframework.context.annotation.ConfigurationClassPostProcessor
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

	/**
	 * 在应用程序上下文的标准初始化之后修改其内部bean定义注册表。
	 * 所有常规bean定义都已加载，但还没有实例化任何bean。
	 * 这允许在下一个后处理阶段开始之前添加更多的bean定义。
	 *
	 * Modify the application context's internal bean definition registry after its
	 * standard initialization. All regular bean definitions will have been loaded,
	 * but no beans will have been instantiated yet. This allows for adding further
	 * bean definitions before the next post-processing phase kicks in.
	 * @param registry the bean definition registry used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
