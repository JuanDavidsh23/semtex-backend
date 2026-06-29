package com.semtex.infrastructure.out.ai;

import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configura el modelo de chat de Ollama (Llama 3.1 8B con tool-calling nativo).
 *
 * Solo se activa con {@code semtex.ai.provider=ollama} (modo local/privado). El proveedor por
 * defecto es OpenAI ({@link OpenAiConfig}). El timeout evita que una inferencia colgada bloquee el hilo.
 */
@Configuration
public class OllamaConfig {

    @Bean
    @ConditionalOnProperty(name = "semtex.ai.provider", havingValue = "ollama")
    public OllamaChatModel ollamaChatModel(
            @Value("${semtex.ai.base-url:http://localhost:11434}") String baseUrl,
            @Value("${semtex.ai.model:llama3.1:8b}") String model,
            @Value("${semtex.ai.timeout-seconds:120}") long timeoutSeconds) {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(model)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .temperature(0.2)
                .build();
    }
}
