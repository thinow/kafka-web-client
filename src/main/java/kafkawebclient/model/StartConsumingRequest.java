package kafkawebclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartConsumingRequest {
    private String cluster;
    private String topic;
    private String maxMessages; // TODO change the type to Long
}
