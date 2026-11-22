package dev.udaylabs.reviewbuddy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/** Makes {@code @ApplicationModuleListener} handlers run asynchronously (off the request thread). */
@Configuration(proxyBeanMethods = false)
@EnableAsync
class AsyncConfig {}
