package se.asplund;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Component
@Path("/secret")
public class SecretStuffController extends SecuredController {
	private static final Logger logger = LoggerFactory.getLogger(SecretStuffController.class);

	@GET
	@Produces("text/plain")
	public void asyncGetOne(@Suspended final AsyncResponse response) {
		handleResponse(response);
	}

	@Override
	protected String getApiMessage() {
		return "Hello";
	}

}
