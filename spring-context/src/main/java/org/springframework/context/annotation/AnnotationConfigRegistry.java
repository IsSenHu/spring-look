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

package org.springframework.context.annotation;

/**
 * 注释配置应用程序上下文的公共接口，定义#register和#scan方法。
 *
 * Common interface for annotation config application contexts,
 * defining {@link #register} and {@link #scan} methods.
 *
 * @author Juergen Hoeller
 * @since 4.1
 */
public interface AnnotationConfigRegistry {

	/**
	 * 注册一个或多个要处理的组件类。
	 * 对register的调用是幂等的；多次添加相同的组件类不会产生任何其他影响。
	 *
	 * Register one or more component classes to be processed.
	 * <p>Calls to {@code register} are idempotent; adding the same
	 * component class more than once has no additional effect.
	 * @param componentClasses one or more component classes,
	 * e.g. {@link Configuration @Configuration} classes
	 */
	void register(Class<?>... componentClasses);

	/**
	 * 在指定的基本程序包中执行扫描。
	 *
	 * Perform a scan within the specified base packages.
	 * @param basePackages the packages to scan for component classes
	 */
	void scan(String... basePackages);

}
