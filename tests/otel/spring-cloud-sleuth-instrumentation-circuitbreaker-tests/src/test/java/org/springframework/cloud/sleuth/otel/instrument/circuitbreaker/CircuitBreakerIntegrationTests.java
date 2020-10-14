/*
 * Copyright 2013-2020 the original author or authors.
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

package org.springframework.cloud.sleuth.otel.instrument.circuitbreaker;

import io.opentelemetry.common.AttributeKey;
import io.opentelemetry.sdk.trace.Sampler;
import io.opentelemetry.sdk.trace.Samplers;
import org.assertj.core.api.BDDAssertions;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.sleuth.otel.exporter.ArrayListSpanProcessor;
import org.springframework.cloud.sleuth.otel.AssertingThrowable;
import org.springframework.cloud.sleuth.otel.OtelTestSpanHandler;
import org.springframework.cloud.sleuth.test.ReportedSpan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CircuitBreakerIntegrationTests.Config.class)
public class CircuitBreakerIntegrationTests
		extends org.springframework.cloud.sleuth.instrument.circuitbreaker.CircuitBreakerIntegrationTests {

	@Configuration(proxyBeanMethods = false)
	static class Config {

		@Bean
		OtelTestSpanHandler testSpanHandlerSupplier() {
			return new OtelTestSpanHandler(new ArrayListSpanProcessor());
		}

		@Bean
		Sampler alwaysSampler() {
			return Samplers.alwaysOn();
		}

	}

	@Override
	public void assertException(ReportedSpan reportedSpan) {
		AssertingThrowable throwable = (AssertingThrowable) reportedSpan.error();
		String msg = throwable.attributes.get(AttributeKey.stringKey("exception.message"));
		BDDAssertions.then(msg).contains("boom");
	}

}