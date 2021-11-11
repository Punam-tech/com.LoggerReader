package com.loggerreader;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionReader {
	private String id;
    private State state;
    private String type;
    private String host;
    private Long timestamp;

    @JsonCreator
    TransactionReader(@JsonProperty(value="id", required = true) String id, @JsonProperty(value="state", required = true) State state, @JsonProperty("type") String type,
        @JsonProperty("host") String host, @JsonProperty(value="timestamp", required = true) Long timestamp) {
        this.id = id;
        this.state = state;
        this.type = type;
        this.host = host;
        this.timestamp = timestamp;
    }
    public String getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public Long getTimestamp() {
        return timestamp;
    }


    public static enum State {
        @JsonProperty("STARTED")
        STARTED,
        @JsonProperty("FINISHED")
        FINISHED
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TransactionReader tr = (TransactionReader) obj;
        return Objects.equals(id, tr.id) &&
                state == tr.state &&
                Objects.equals(type, tr.type) &&
                Objects.equals(host, tr.host) &&
                Objects.equals(timestamp, tr.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, type, host, timestamp);
    }

    @Override
    public String toString() {
        return "TransactionReader{" +
                "id='" + id + '\'' +
                ", state=" + state +
                ", type='" + type + '\'' +
                ", host='" + host + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

}
