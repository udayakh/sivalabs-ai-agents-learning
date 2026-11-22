package dev.udaylabs.reviewbuddy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Enables Spring Data auditing so that created_at / updated_at are populated automatically. */
@Configuration(proxyBeanMethods = false)
@EnableJpaAuditing
class PersistenceConfig {}
