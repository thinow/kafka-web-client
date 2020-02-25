package kafkawebclient.controller;

import kafkawebclient.kafka.KafkaPoller;
import kafkawebclient.kafka.KafkaPollerFactory;
import kafkawebclient.model.StartConsumingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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
    public void start(StartConsumingRequest request) throws Exception {
        LOG.debug("Received request : {}", request);
        try (KafkaPoller poller = kafkaPollerFactory.createPoller(request.getCluster(), singleton(request.getTopic()))) {
            poller.poll(request.getMaxMessages())
                    .forEach(message -> messagingTemplate.convertAndSend(QUEUES_PREFIX + "/consumed-message", message));
        }
        messagingTemplate.convertAndSend(QUEUES_PREFIX + "/end", ""); // TODO send something more explicit than empty String
    }
}
