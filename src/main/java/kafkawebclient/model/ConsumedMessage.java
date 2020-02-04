package kafkawebclient.model;

public class ConsumedMessage {
    private long index;
    private long offset;
    private String timestamp;
    private Object value;

    public ConsumedMessage(long index, long offset, String timestamp, Object value) {
        this.index = index;
        this.offset = offset;
        this.timestamp = timestamp;
        this.value = value;
    }

    public long getIndex() {
        return index;
    }

    public long getOffset() {
        return offset;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Object getValue() {
        return value;
    }
}
