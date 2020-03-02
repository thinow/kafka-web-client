package kafkawebclient.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class KafkaPollerConfiguration {

    @Value("${KAWC_SESSION_TIMEOUT_IN_MS:300000}") // default = 5 minutes
    private long sessionTimeoutInMilliseconds;

    @Value("${KAWC_POLL_TIMEOUT_IN_MS:1000}") // default = 1 second
    private long pollTimeoutInMilliseconds;

    public Duration getSessionTimeout() {
        return Duration.ofMillis(sessionTimeoutInMilliseconds);
    }

    public Duration getPollTimeout() {
        return Duration.ofMillis(pollTimeoutInMilliseconds);
    }
}
