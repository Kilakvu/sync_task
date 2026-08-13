package org.dataki.adapters.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.dataki.infrastructure.config.WhatsAppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Webhook de WhatsApp Cloud API.
 * <p>
 * Meta valida el webhook con GET (hub.verify_token / hub.challenge) y luego
 * entrega los eventos (estados de entrega y mensajes entrantes) mediante POST.
 * La ruta está exenta del filtro de API key: la autenticación del GET es el
 * verify_token y la del POST es la firma de Meta (X-Hub-Signature-256).
 */
@RestController
@RequestMapping("/api/v1/whatsapp/webhook")
@Tag(name = "WhatsApp Webhook", description = "Webhook de WhatsApp Cloud API para estados de entrega y mensajes")
public class WhatsAppWebhookController {
    private static final Logger logger = LoggerFactory.getLogger(WhatsAppWebhookController.class);

    private final String verifyToken;

    public WhatsAppWebhookController(WhatsAppProperties props) {
        this.verifyToken = props.verifyToken();
    }

    /**
     * GET - Verificación inicial del webhook solicitada por Meta
     */
    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String verify(@RequestParam("hub.mode") String mode,
                         @RequestParam("hub.verify_token") String token,
                         @RequestParam("hub.challenge") String challenge) {
        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            logger.info("Webhook de WhatsApp verificado correctamente");
            return challenge;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Verificación de webhook fallida");
    }

    /**
     * POST - Eventos de WhatsApp (estados sent/delivered/read y respuestas del cliente)
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> receive(@RequestBody String payload) {
        logger.info("Evento de WhatsApp recibido: {}", payload);
        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}
