package se.asplund;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Component
public class NonSecretStuffController extends OpenController {
	@Autowired
	private AsyncMqClient mqClient;

	@GET
	@Produces("text/plain")
	@Path("{echo}")
	public void asyncGetOne(@Suspended final AsyncResponse response, @PathParam("echo") String echo) {
		getObservable()
				.flatMap(msg -> mqClient.sendAsynchronous(msg.withBody(echo)))
				.map(msg -> msg.getMessageBody() + " " + msg.getSecurityContext())
				.subscribe(response::resume, response::resume);
	}
}
