package kafkawebclient;

import kafkawebclient.kafka.KafkaProducer;
import kafkawebclient.model.KafkaMessage;
import kafkawebclient.model.StartConsumingRequest;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static kafkawebclient.config.WebSocketConfig.QUEUES_PREFIX;
import static kafkawebclient.model.FetchMethod.OLDEST;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketControllerIntegrationTest {

    public static final String KAFKA_SERVER = "127.0.0.1:9092";
    @LocalServerPort
    private int port;

    private WebSocketStompClient client;
    private StompSession session;

    @BeforeEach
    public void setUp() throws Exception {
        client = createWebSocketClient();

        final String url = String.format("ws://127.0.0.1:%d/stomp", port);
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
    public void receivesSingleMessageFromSubscribedQueue() throws Exception {
        // given
        final String topic = "kawc-test-topic-" + UUID.randomUUID();

        KafkaProducer.produce(KAFKA_SERVER, topic, 1L);

        final CompletableFuture<KafkaMessage> response = subscribe(
                QUEUES_PREFIX + "/consumed-message", KafkaMessage.class);
        final CompletableFuture<Object> end = subscribe(
                QUEUES_PREFIX + "/end", Object.class);

        // when
        session.send("/start", new StartConsumingRequest(KAFKA_SERVER, topic, 1L, OLDEST));
        waitFor(response);
        waitFor(end);

        // then
        assertThat(response.get().getValue()).isNotNull();
    }

    private <T> CompletableFuture<T> subscribe(String queue, Class<T> type) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        session.subscribe(queue, new GenericStompFrameHandler<T>(type, future::complete));
        return future;
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

    private <T> T waitFor(CompletableFuture<T> future) throws Exception {
        return future.get(30, TimeUnit.SECONDS);
    }
}