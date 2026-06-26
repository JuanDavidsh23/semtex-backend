package com.semtex.audit.infrastructure.aspect;

import com.semtex.audit.application.AuditRecorder;
import com.semtex.audit.domain.model.AuditLog;
import com.semtex.chat.domain.port.in.ChatUseCase.SendMessageCommand;
import com.semtex.document.domain.model.Document;
import com.semtex.email.domain.port.in.SendEmailUseCase.SendEmailCommand;
import com.semtex.identity.domain.model.User;
import com.semtex.shared.tenant.TenantContext;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Auditoría transversal por AOP: registra las acciones relevantes (del agente y de gestión) sin
 * que cada servicio tenga que llamar a la auditoría manualmente. Vive en el contexto audit como
 * observador de los demás contextos.
 */
@Aspect
@Component
public class AgentAuditAspect {

    private final AuditRecorder recorder;

    public AgentAuditAspect(AuditRecorder recorder) {
        this.recorder = recorder;
    }

    @AfterReturning("execution(* com.semtex.email.application.EmailService.send(..)) && args(command)")
    public void onEmailSent(SendEmailCommand command) {
        recorder.record(AuditLog.emailSent(
                command.organizationId(), command.requestedByUserId(), command.toAddress()));
    }

    @AfterThrowing(pointcut = "execution(* com.semtex.email.application.EmailService.send(..)) && args(command)",
            throwing = "ex")
    public void onEmailFailed(SendEmailCommand command, Throwable ex) {
        recorder.record(AuditLog.emailFailed(
                command.organizationId(), command.requestedByUserId(), command.toAddress(), ex.getMessage()));
    }

    @AfterReturning(pointcut = "execution(* com.semtex.document.application.DocumentService.upload(..))",
            returning = "document")
    public void onDocumentUploaded(Document document) {
        recorder.record(AuditLog.documentUploaded(
                document.getOrganizationId(), document.getUploadedBy(), document.getName()));
    }

    @AfterReturning("execution(* com.semtex.chat.application.ChatService.sendMessage(..)) && args(command)")
    public void onChatQuery(SendMessageCommand command) {
        recorder.record(AuditLog.financialQuery(
                command.organizationId(), command.userId(), command.content()));
    }

    @AfterReturning(pointcut = "execution(* com.semtex.identity.application.UserService.create(..))",
            returning = "user")
    public void onUserCreated(User user) {
        recorder.record(AuditLog.userCreated(
                user.getOrganizationId(), TenantContext.currentUserId(), user.getEmail()));
    }
}
