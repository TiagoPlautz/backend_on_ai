package com.aiprocess.backendonia.config;

import com.aiprocess.backendonia.ai.DocumentationAssistant;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    DocumentationAssistant documentationAssistant(AppProperties properties) {
        String apiKey = properties.getAi().getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            return prompt -> """
                    A integração com o modelo está pronta, mas nenhuma chave da Anthropic foi configurada.
                    Salvei a conversa e respondi em modo local. Defina a variável de ambiente ANTHROPIC_API_KEY para ativar a IA real.
                    """;
        }

        var chatModel = AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName(properties.getAi().getModelName())
                .temperature(properties.getAi().getTemperature())
                .build();
        return AiServices.create(DocumentationAssistant.class, chatModel);
    }
}
