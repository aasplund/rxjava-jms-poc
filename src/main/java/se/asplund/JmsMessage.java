package se.asplund;

import java.io.Serializable;

final public class JmsMessage<T> implements Serializable {
	final private String id;
	final private T message;

	public JmsMessage(final String id, final T message) {
		this.id = id;
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public T getMessage() {
		return message;
	}
}
