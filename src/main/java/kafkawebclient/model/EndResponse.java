package kafkawebclient.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EndResponse {
    private String status;
    private String message;

    public static EndResponse success() {
        return EndResponse.builder()
                .status("success")
                .build();
    }

    public static EndResponse error(Throwable throwable) {
        return EndResponse.builder()
                .status("error")
                .message(String.join(". ", findMessages(throwable)))
                .build();
    }

    private static List<String> findMessages(Throwable throwable) {
        if (throwable.getCause() == null) {
            return List.of(throwable.getMessage());
        } else {
            return List.of(throwable.getMessage(), throwable.getCause().getMessage());
        }
    }
}
