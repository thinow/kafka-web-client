package kafkawebclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartConsumingRequest {
    private String cluster; // TODO rename to server or servers
    private String topic;
    private long maxMessages;
    private FetchMethod method;
}
