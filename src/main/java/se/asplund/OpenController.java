package se.asplund;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.ws.rs.Path;

@Path("/")
public abstract class OpenController extends AbstractAsyncController {
	private static final Logger logger = LoggerFactory.getLogger(OpenController.class);

	@Override
	protected Observable<ApiMessage> createObservable() {
		logger.debug("Unsecured request received");
		return Observable.just(new ApiMessage().withSecurityContext("Anonymous"));
	}

}
