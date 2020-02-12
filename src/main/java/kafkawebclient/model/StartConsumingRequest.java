package kafkawebclient.model;

import lombok.Data;

@Data
public class StartConsumingRequest {
    private String cluster;
    private String topic;
    private String maxMessages; // TODO change the type to Long
}
