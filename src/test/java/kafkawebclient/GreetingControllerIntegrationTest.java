package kafkawebclient;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GreetingControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient client;
    private StompSession session;

    @BeforeEach
    public void setUp() throws Exception {
        client = createWebSocketClient();

        final String url = String.format("ws://127.0.0.1:%d/gs-guide-websocket", port);
        session = client.connect(url, new SimpleStompSessionHandler()).get();
    }

    static class SimpleStompSessionHandler extends StompSessionHandlerAdapter {
    }

    private WebSocketStompClient createWebSocketClient() {
        WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new MappingJackson2MessageConverter());
        return client;
    }

    @AfterEach
    public void tearDown() {
        session.disconnect();
        client.stop();
    }

    @Test
    public void connectsToSocket() {
        assertThat(session.isConnected()).isTrue();
    }

    @Test
    public void receivesMessageFromSubscribedQueue() throws Exception {
        //given
        CompletableFuture<Greeting> future = new CompletableFuture<>();

        session.subscribe(
                "/topic/greetings",
                new GenericStompFrameHandler<>(Greeting.class, future::complete)
        );

        //when
        session.send("/app/hello", new HelloMessage("Sam"));

        //then
        final Greeting message = future.get(2, TimeUnit.SECONDS);
        assertThat(message.getContent()).isEqualTo("Hello, Sam!");
    }

    public static class GenericStompFrameHandler<T> implements StompFrameHandler {

        private final Class<T> payloadType;
        private final Consumer<T> frameHandler;

        public GenericStompFrameHandler(Class<T> payloadType, Consumer<T> frameHandler) {
            this.payloadType = payloadType;
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return payloadType;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            frameHandler.accept(payloadType.cast(payload));
        }
    }
}