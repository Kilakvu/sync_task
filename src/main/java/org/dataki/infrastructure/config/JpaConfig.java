package org.dataki.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuración de JPA y acceso a datos
 */
@Configuration
@EnableJpaRepositories(basePackages = "org.dataki.infrastructure.persistence.repository")
public class JpaConfig {
}
