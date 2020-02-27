package kafkawebclient.controller;

import kafkawebclient.kafka.KafkaPoller;
import kafkawebclient.kafka.KafkaPollerFactory;
import kafkawebclient.model.FetchMethod;
import kafkawebclient.model.StartConsumingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Set;

import static java.util.Collections.singleton;
import static kafkawebclient.config.WebSocketConfig.QUEUES_PREFIX;

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

        try (KafkaPoller poller = kafkaPollerFactory.createPoller(cluster, topics, method)) {
            poller.poll(request.getMaxMessages())
                    .forEach(message -> messagingTemplate.convertAndSend(QUEUES_PREFIX + "/consumed-message", message));
        }
        messagingTemplate.convertAndSend(QUEUES_PREFIX + "/end", ""); // TODO send something more explicit than empty String
    }
}
