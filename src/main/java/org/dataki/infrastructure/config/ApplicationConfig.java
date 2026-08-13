package org.dataki.infrastructure.config;

import org.dataki.application.service.CreateCitaService;
import org.dataki.application.service.RetrieveCitaService;
import org.dataki.application.service.CitaProcessorService;
import org.dataki.domain.port.output.CitaProcessor;
import org.dataki.domain.port.output.CitaRepository;
import org.dataki.domain.service.CitaDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuración de beans y aplicación
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ApplicationConfig {
    
    @Bean
    public CitaDomainService citaDomainService(CitaRepository citaRepository) {
        return new CitaDomainService(citaRepository);
    }

    @Bean
    public CreateCitaService createCitaService(CitaRepository citaRepository, CitaProcessor citaProcessor) {
        return new CreateCitaService(citaRepository, citaProcessor);
    }

    @Bean
    public RetrieveCitaService retrieveCitaService(CitaRepository citaRepository) {
        return new RetrieveCitaService(citaRepository);
    }

    @Bean
    public CitaProcessorService citaProcessorService(CitaRepository citaRepository) {
        return new CitaProcessorService(citaRepository);
    }
}
