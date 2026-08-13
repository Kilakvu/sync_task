package org.dataki.infrastructure.adapter;

import org.dataki.domain.model.Cita;
import org.dataki.domain.port.output.CitaRepository;
import org.dataki.infrastructure.config.WhatsAppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adaptador de recordatorios por WhatsApp Cloud API (Meta).
 * <p>
 * Activo con <code>app.notifications.mode=whatsapp</code>.
 * Envía una plantilla aprobada en Meta (por defecto <code>cita_recordatorio</code>)
 * con los datos del cliente, el servicio y la fecha de la cita.
 */
@Component
@ConditionalOnProperty(name = "app.notifications.mode", havingValue = "whatsapp")
public class WhatsAppCitaProcessor extends AbstractCitaProcessor {
    private static final Logger logger = LoggerFactory.getLogger(WhatsAppCitaProcessor.class);

    private final WhatsAppProperties props;
    private final RestClient restClient;

    public WhatsAppCitaProcessor(CitaRepository citaRepository, WhatsAppProperties props) {
        super(citaRepository);
        this.props = props;
        this.restClient = RestClient.builder()
                .baseUrl(props.apiUrl())
                .requestFactory(jdkClientHttpRequestFactory())
                .build();
    }

    @Override
    public void processCita(Cita cita) {
        if (props.accessToken() == null || props.accessToken().isBlank()
                || props.phoneNumberId() == null || props.phoneNumberId().isBlank()) {
            throw new IllegalStateException(
                    "Falta configuración de WhatsApp (WHATSAPP_ACCESS_TOKEN / WHATSAPP_PHONE_NUMBER_ID)");
        }

        logger.info("Enviando recordatorio por WhatsApp para la cita {} al teléfono {}", cita.getId(), cita.getPhone());
        try {
            Map<?, ?> response = restClient.post()
                    .uri("/{version}/{phoneNumberId}/messages", props.graphApiVersion(), props.phoneNumberId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + props.accessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildTemplateMessage(cita))
                    .retrieve()
                    .body(Map.class);
            logger.info("WhatsApp Cloud API respondió para la cita {}: {}", cita.getId(), response);
        } catch (RestClientException e) {
            logger.error("Error enviando mensaje de WhatsApp para la cita {}: {}", cita.getId(), e.getMessage());
            throw e;
        }
    }

    private Map<String, Object> buildTemplateMessage(Cita cita) {
        Map<String, Object> template = new LinkedHashMap<>();
        template.put("name", props.templateName());
        template.put("language", Map.of("code", props.templateLanguage()));
        template.put("components", List.of(Map.of(
                "type", "body",
                "parameters", List.of(
                        Map.of("type", "text", "text", cita.getCustomerName()),
                        Map.of("type", "text", "text", cita.getService()),
                        Map.of("type", "text", "text", formatFecha(cita))))));

        Map<String, Object> message = new LinkedHashMap<>();
        message.put("messaging_product", "whatsapp");
        message.put("to", normalizePhone(cita.getPhone()));
        message.put("type", "template");
        message.put("template", template);
        return message;
    }

    private String normalizePhone(String phone) {
        return phone.replaceAll("[^0-9]", "");
    }

    private JdkClientHttpRequestFactory jdkClientHttpRequestFactory() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(15));
        return factory;
    }
}
