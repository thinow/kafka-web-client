package kafkawebclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessage {
    private int partition;
    private long offset;
    private String timestamp;
    private String value;
}
