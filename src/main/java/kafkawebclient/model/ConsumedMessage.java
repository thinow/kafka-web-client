package kafkawebclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumedMessage {
    private long index;
    private long offset;
    private String timestamp;
    private Object value;
}
