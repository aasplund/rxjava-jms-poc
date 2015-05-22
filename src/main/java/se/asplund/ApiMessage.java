package se.asplund;


import java.io.Serializable;

public class ApiMessage implements Serializable {
	private String messageBody;
	private String securityContext;

	public ApiMessage withSecurityContext(String securityContext) {
		this.securityContext = securityContext;
		return this;
	}

	public ApiMessage withBody(String messageBody) {
		this.messageBody = messageBody;
		return this;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public String getSecurityContext() {
		return securityContext;
	}
}
