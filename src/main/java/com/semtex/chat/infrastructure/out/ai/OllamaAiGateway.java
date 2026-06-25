package com.semtex.chat.infrastructure.out.ai;

import com.semtex.chat.domain.port.out.AiGatewayPort;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import org.springframework.stereotype.Component;

/**
 * Adaptador de IA sobre LangChain4j + Ollama (chat de un turno con contexto financiero inyectado).
 *
 * El soporte de function calling (herramientas) lo añade el contexto del agente sobre este mismo
 * modelo. Aquí solo se hace la inferencia básica, ya con timeout y fuera de transacción.
 */
@Component
public class OllamaAiGateway implements AiGatewayPort {

    private static final String SYSTEM_PROMPT = """
            Eres Semtex, un copiloto financiero y administrativo para PyMEs.
            Tienes acceso a los registros contables de la empresa que se te proporcionan como contexto.
            Responde siempre en español, de forma clara y concisa.
            Cuando hagas cálculos financieros, muestra el desglose.
            Si no tienes datos suficientes, dilo con honestidad.
            """;

    private final OllamaChatModel model;

    public OllamaAiGateway(OllamaChatModel model) {
        this.model = model;
    }

    @Override
    public AiAnswer ask(AiRequest request) {
        String contextText = request.contextText() == null || request.contextText().isBlank()
                ? "No hay datos financieros cargados."
                : request.contextText();

        SystemMessage system = SystemMessage.from(
                SYSTEM_PROMPT + "\n\nDATOS FINANCIEROS DISPONIBLES:\n" + contextText);
        UserMessage user = UserMessage.from(request.userMessage());

        Response<AiMessage> response = model.generate(system, user);
        Integer tokens = totalTokens(response.tokenUsage());
        return new AiAnswer(response.content().text(), tokens);
    }

    private Integer totalTokens(TokenUsage usage) {
        return usage == null ? null : usage.totalTokenCount();
    }
}
