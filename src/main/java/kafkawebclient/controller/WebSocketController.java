package kafkawebclient.controller;

import kafkawebclient.model.ConsumedMessage;
import kafkawebclient.model.StartConsumingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;

import static kafkawebclient.config.WebSocketConfig.QUEUES_PREFIX;

@Controller
public class WebSocketController {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketController.class);
    private SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/start")
    public void start(StartConsumingRequest request) throws Exception {
        LOG.info("Received request : " + request);
        messagingTemplate.convertAndSend(QUEUES_PREFIX + "/consumed-message", generateMessage(request));
        Thread.sleep(500L); // simulating a delay
        messagingTemplate.convertAndSend(QUEUES_PREFIX + "/end", "");
    }

    private ConsumedMessage generateMessage(StartConsumingRequest request) {
        return new ConsumedMessage(123, 456, Instant.now().toString(), new AnyObject(request.getTopic()));
    }

    static class AnyObject {
        private String innerValue;

        public AnyObject(String innerValue) {
            this.innerValue = innerValue;
        }

        public String getInnerValue() {
            return innerValue;
        }
    }
}
