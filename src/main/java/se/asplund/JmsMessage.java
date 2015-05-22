package se.asplund;

import java.io.Serializable;

final public class JmsMessage implements Serializable {
	final private String id;
	final private Object message;

	public JmsMessage(final String id, final Object message) {
		this.id = id;
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public Object getMessage() {
		return message;
	}
}
