package com.aiprocess.backendonia.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AiStartupLogger {

    private static final Logger log = LoggerFactory.getLogger(AiStartupLogger.class);

    private final AppProperties properties;

    public AiStartupLogger(AppProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void logAiConfiguration() {
        boolean configured = properties.getAi().getApiKey() != null && !properties.getAi().getApiKey().isBlank();
        log.info("Anthropic API key carregada: {}", configured ? "SIM" : "NAO");
        log.info("Origem esperada da chave: application.yml -> app.ai.api-key");
        log.info("Provider configurado: {}", properties.getAi().getProvider());
        log.info("Modelo configurado: {}", properties.getAi().getModelName());
    }
}
