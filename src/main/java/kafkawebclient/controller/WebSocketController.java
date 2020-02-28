package kafkawebclient.controller;

import kafkawebclient.kafka.KafkaPoller;
import kafkawebclient.kafka.KafkaPollerFactory;
import kafkawebclient.model.EndResponse;
import kafkawebclient.model.FetchMethod;
import kafkawebclient.model.StartConsumingRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Set;

import static java.util.Collections.singleton;
import static kafkawebclient.config.WebSocketConfig.QUEUES_PREFIX;

@Slf4j
@Controller
public class WebSocketController {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaPollerFactory kafkaPollerFactory;

    public WebSocketController(SimpMessagingTemplate messagingTemplate, KafkaPollerFactory kafkaPollerFactory) {
        this.messagingTemplate = messagingTemplate;
        this.kafkaPollerFactory = kafkaPollerFactory;
    }

    @MessageMapping("/start")
    public void start(StartConsumingRequest request) {
        LOG.debug("Received request : {}", request);

        final String cluster = request.getCluster();
        final Set<String> topics = singleton(request.getTopic());
        final FetchMethod method = request.getMethod();

        try {
            try (KafkaPoller poller = kafkaPollerFactory.createPoller(cluster, topics, method)) {
                poller.poll(request.getMaxMessages()).forEach(message -> send("/consumed-message", message));
            }
            send("/end", EndResponse.success());
        } catch (Exception cause) {
            log.error("Error when consuming", cause);
            send("/end", EndResponse.error(cause));
        }
    }

    private void send(String queue, Object payload) {
        messagingTemplate.convertAndSend(QUEUES_PREFIX + queue, payload);
    }
}
