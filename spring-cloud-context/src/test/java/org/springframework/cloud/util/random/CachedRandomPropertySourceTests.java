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

package org.springframework.cloud.util.random;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.env.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Ryan Baxter
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
@DirtiesContext
public class CachedRandomPropertySourceTests {

	@Mock
	private PropertySource randomValuePropertySource;

	@BeforeEach
	public void setup() {
		when(randomValuePropertySource.getProperty(eq("random.long"))).thenReturn(1234L);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getProperty(CapturedOutput output) {
		Map<String, Map<String, Object>> cache = new HashMap<>();
		Map<String, Object> typeCache = new HashMap<>();
		typeCache.put("long", 5678L);
		cache.put("foo", typeCache);

		CachedRandomPropertySource cachedRandomPropertySource = new CachedRandomPropertySource(
				randomValuePropertySource, cache);
		then(cachedRandomPropertySource.getProperty("foo.app.long")).isNull();
		then(cachedRandomPropertySource.getProperty("cachedrandom.app")).isNull();

		then(cachedRandomPropertySource.getProperty("cachedrandom.app.long")).isEqualTo(1234L);
		then(cachedRandomPropertySource.getProperty("cachedrandom.foo.long")).isEqualTo(5678L);
		then(StringUtils.countOccurrencesOf(output.toString(), "No cached value found for key: app")).isEqualTo(1);
		then(StringUtils.countOccurrencesOf(output.toString(),
				"No random value found in cache for key: app and type: long, generating a new value")).isEqualTo(1);
		then(StringUtils.countOccurrencesOf(output.toString(), "No cached value found for key: foo")).isEqualTo(0);
		then(StringUtils.countOccurrencesOf(output.toString(),
				"No random value found in cache for key: foot and type: long, generating a new value")).isEqualTo(0);
	}

}
