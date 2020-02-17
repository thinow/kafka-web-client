package kafkawebclient.controller;

import kafkawebclient.kafka.KafkaConsumerFactory;
import kafkawebclient.kafka.PollingSession;
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
    private final KafkaConsumerFactory kafkaConsumerFactory;

    public WebSocketController(SimpMessagingTemplate messagingTemplate, KafkaConsumerFactory kafkaConsumerFactory) {
        this.messagingTemplate = messagingTemplate;
        this.kafkaConsumerFactory = kafkaConsumerFactory;
    }

    @MessageMapping("/start")
    public void start(StartConsumingRequest request) throws Exception {
        LOG.debug("Received request : {}", request);
        try (PollingSession session = kafkaConsumerFactory.consume(request.getCluster(), singleton(request.getTopic()))) {
            session.poll(request.getMaxMessages())
                    .forEach(message -> messagingTemplate.convertAndSend(QUEUES_PREFIX + "/consumed-message", message));
        }
        messagingTemplate.convertAndSend(QUEUES_PREFIX + "/end", ""); // TODO send something more explicit than empty String
    }
}
