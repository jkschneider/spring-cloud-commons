/*
 * Copyright 2012-2020 the original author or authors.
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

package org.springframework.cloud.loadbalancer.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ConditionalOnBlockingDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnReactiveDiscoveryEnabled;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

/**
 * @author Spencer Gibb
 * @author Olga Maciaszek-Sharma
 * @author Tim Ysewyn
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
public class LoadBalancerClientConfiguration {

	private static final int REACTIVE_SERVICE_INSTANCE_SUPPLIER_ORDER = 193827465;

	@Bean
	@ConditionalOnMissingBean
	ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
			Environment environment,
			LoadBalancerClientFactory loadBalancerClientFactory) {
		String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
		return new RoundRobinLoadBalancer(loadBalancerClientFactory.getLazyProvider(name,
				ServiceInstanceListSupplier.class), name);
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnReactiveDiscoveryEnabled
	@Order(REACTIVE_SERVICE_INSTANCE_SUPPLIER_ORDER)
	public static class ReactiveSupportConfiguration {

		@Bean
		@ConditionalOnBean(ReactiveDiscoveryClient.class)
		@ConditionalOnMissingBean
		@ConditionalOnProperty(value = "spring.cloud.loadbalancer.configurations",
				havingValue = "default", matchIfMissing = true)
		ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
				ConfigurableApplicationContext context) {
			return ServiceInstanceListSupplier.builder().withDiscoveryClient()
					.withCaching().build(context);
		}

		@Bean
		@ConditionalOnBean(ReactiveDiscoveryClient.class)
		@ConditionalOnMissingBean
		@ConditionalOnProperty(value = "spring.cloud.loadbalancer.configurations",
				havingValue = "zone-preference")
		ServiceInstanceListSupplier zonePreferenceDiscoveryClientServiceInstanceListSupplier(
				ConfigurableApplicationContext context) {
			return ServiceInstanceListSupplier.builder().withDiscoveryClient()
					.withZonePreference().withCaching().build(context);
		}

		@Bean
		@ConditionalOnBean(ReactiveDiscoveryClient.class)
		@ConditionalOnMissingBean
		@ConditionalOnProperty(value = "spring.cloud.loadbalancer.configurations",
				havingValue = "health-check")
		ServiceInstanceListSupplier healthCheckDiscoveryClientServiceInstanceListSupplier(
				ConfigurableApplicationContext context) {
			return ServiceInstanceListSupplier.builder().withDiscoveryClient()
					.withHealthChecks().withCaching().build(context);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnBlockingDiscoveryEnabled
	@Order(REACTIVE_SERVICE_INSTANCE_SUPPLIER_ORDER + 1)
	public static class BlockingSupportConfiguration {

		@Bean
		@ConditionalOnBean(DiscoveryClient.class)
		@ConditionalOnMissingBean
		@ConditionalOnProperty(value = "spring.cloud.loadbalancer.configurations",
				havingValue = "default", matchIfMissing = true)
		ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
				ConfigurableApplicationContext context) {
			return ServiceInstanceListSupplier.builder().withBlockingDiscoveryClient()
					.withCaching().build(context);
		}

		@Bean
		@ConditionalOnBean(DiscoveryClient.class)
		@ConditionalOnMissingBean
		@ConditionalOnProperty(value = "spring.cloud.loadbalancer.configurations",
				havingValue = "zone-preference")
		ServiceInstanceListSupplier zonePreferenceDiscoveryClientServiceInstanceListSupplier(
				ConfigurableApplicationContext context) {
			return ServiceInstanceListSupplier.builder().withBlockingDiscoveryClient()
					.withZonePreference().withCaching().build(context);
		}

		@Bean
		@ConditionalOnBean(DiscoveryClient.class)
		@ConditionalOnMissingBean
		@ConditionalOnProperty(value = "spring.cloud.loadbalancer.configurations",
				havingValue = "health-check")
		ServiceInstanceListSupplier healthCheckDiscoveryClientServiceInstanceListSupplier(
				ConfigurableApplicationContext context) {
			return ServiceInstanceListSupplier.builder().withBlockingDiscoveryClient()
					.withHealthChecks().withCaching().build(context);
		}

	}

}
