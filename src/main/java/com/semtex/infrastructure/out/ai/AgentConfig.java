package com.semtex.infrastructure.out.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Construye el {@link SemtexAgent} con {@code AiServices}, enlazando el modelo de chat (OpenAI por
 * defecto, u Ollama) y las herramientas. AiServices implementa el bucle de tool-calling automáticamente.
 */
@Configuration
public class AgentConfig {

    @Bean
    public SemtexAgent semtexAgent(ChatLanguageModel model, SemtexAgentTools tools) {
        return AiServices.builder(SemtexAgent.class)
                .chatLanguageModel(model)
                .tools(tools)
                .build();
    }
}
