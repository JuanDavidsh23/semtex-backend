package com.semtex.infrastructure.out.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Modelo de chat sobre una API compatible con OpenAI (OpenAI, o Groq cambiando la base-url),
 * con tool-calling.
 *
 * Es el proveedor por defecto: hospedado, sin GPU propia, funciona en Render. Se activa con
 * {@code semtex.ai.provider=openai} (por defecto). Para volver a Ollama local: {@code =ollama}.
 *
 * Para usar Groq en vez de OpenAI: poner {@code semtex.ai.openai.base-url=https://api.groq.com/openai/v1}
 * y un modelo Llama en {@code semtex.ai.openai.model}.
 */
@Configuration
public class OpenAiConfig {

    @Bean
    @ConditionalOnProperty(name = "semtex.ai.provider", havingValue = "openai", matchIfMissing = true)
    public ChatLanguageModel openAiChatModel(
            @Value("${semtex.ai.openai.base-url:https://api.openai.com/v1}") String baseUrl,
            @Value("${semtex.ai.openai.api-key:}") String apiKey,
            @Value("${semtex.ai.openai.model:gpt-4o-mini}") String model,
            @Value("${semtex.ai.timeout-seconds:120}") long timeoutSeconds) {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(model)
                .temperature(0.2)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }
}
