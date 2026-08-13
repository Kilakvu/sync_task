package org.dataki.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración de la integración con WhatsApp Cloud API (Meta).
 * <p>
 * En local se usan los valores por defecto o se sobrescriben con variables de entorno:
 * WHATSAPP_ACCESS_TOKEN, WHATSAPP_PHONE_NUMBER_ID, WHATSAPP_VERIFY_TOKEN, etc.
 */
@ConfigurationProperties(prefix = "app.whatsapp")
public record WhatsAppProperties(
        String accessToken,
        String phoneNumberId,
        String verifyToken,
        String apiUrl,
        String graphApiVersion,
        String templateName,
        String templateLanguage
) {
    public WhatsAppProperties {
        if (apiUrl == null || apiUrl.isBlank()) {
            apiUrl = "https://graph.facebook.com";
        }
        if (graphApiVersion == null || graphApiVersion.isBlank()) {
            graphApiVersion = "v21.0";
        }
        if (templateName == null || templateName.isBlank()) {
            templateName = "cita_recordatorio";
        }
        if (templateLanguage == null || templateLanguage.isBlank()) {
            templateLanguage = "es";
        }
        if (verifyToken == null || verifyToken.isBlank()) {
            verifyToken = "whatsapp-dev";
        }
    }
}
