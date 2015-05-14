package se.asplund;

import java.io.Serializable;

public class JmsMessage<T> implements Serializable {
	private String id;
	private T message;

	public JmsMessage(String id, T message) {
		this.id = id;
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}
}
