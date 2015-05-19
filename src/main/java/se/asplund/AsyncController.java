package se.asplund;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.stream.Collectors;

@Component
@Path("/")
public class AsyncController {
	private static final Logger logger = LoggerFactory.getLogger(AsyncController.class);

	@Autowired
	private AsyncMqClient<String> mqClient;

	@GET
	@Produces("text/plain")
	public void asyncGetOne(@Suspended final AsyncResponse response) {
		logger.debug("Request received...");

		mqClient.sendAsynchronous("Hello")
				.map(s -> s.toUpperCase())
				.flatMap(s -> mqClient.sendAsynchronous(s + " World!"))
				.subscribe(response::resume);
	}

	@GET
	@Produces("text/plain")
	@Path("{count}")
	public void asyncGet(@Suspended final AsyncResponse response, @PathParam("count") final int count) {
		logger.debug("Request received...");

		Observable.range(1, count)
				.flatMap(i -> mqClient.sendAsynchronous(i + ". - Hello"))
				.map(s -> s.toUpperCase())
				.flatMap(s -> mqClient.sendAsynchronous(s + " World!"))
				.buffer(count)
				.subscribe(v -> response.resume(v.stream().collect(Collectors.joining("\n"))));
	}
}
