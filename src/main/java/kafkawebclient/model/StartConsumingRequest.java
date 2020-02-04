package kafkawebclient.model;

public class StartConsumingRequest {
    private String cluster;
    private String topic;
    private String maxMessages;

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMaxMessages() {
        return maxMessages;
    }

    public void setMaxMessages(String maxMessages) {
        this.maxMessages = maxMessages;
    }

    @Override
    public String toString() {
        return "StartConsumingRequest{" +
                "cluster='" + cluster + '\'' +
                ", topic='" + topic + '\'' +
                ", maxMessages='" + maxMessages + '\'' +
                '}';
    }
}
