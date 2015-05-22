package se.asplund;


import org.springframework.beans.factory.annotation.Autowired;
import rx.Observable;

import javax.ws.rs.container.AsyncResponse;

public abstract class AbstractAsyncController {

	@Autowired
	private AsyncMqClient mqClient;

	protected abstract Observable<ApiMessage> createObservable();

	protected abstract String getApiMessageBody();

	protected void handleResponse(final AsyncResponse response) {
		createObservable()
				.flatMap(msg -> mqClient.sendAsynchronous(msg.withBody(getApiMessageBody())))
				.map(msg -> msg.getMessageBody() + " " + msg.getSecurityContext())
				.subscribe(response::resume, response::resume);
	}

}
