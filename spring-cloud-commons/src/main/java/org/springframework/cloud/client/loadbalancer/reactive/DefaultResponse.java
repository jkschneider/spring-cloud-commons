/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.client.loadbalancer.reactive;

import org.springframework.cloud.client.ServiceInstance;

/**
 * @author Spencer Gibb
 */
public class DefaultResponse implements Response<ServiceInstance> {

	private final ServiceInstance serviceInstance;

	public DefaultResponse(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	@Override
	public boolean hasServer() {
		return this.serviceInstance != null;
	}

	@Override
	public ServiceInstance getServer() {
		return this.serviceInstance;
	}

	@Override
	public void onComplete(CompletionContext completionContext) {
		// TODO: implement
	}

}