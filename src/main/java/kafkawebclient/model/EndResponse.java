package kafkawebclient.model;

import lombok.Builder;
import lombok.Data;

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

    public static EndResponse error(Throwable cause) {
        return EndResponse.builder()
                .status("error")
                .message(cause.getMessage())
                .build();
    }
}
