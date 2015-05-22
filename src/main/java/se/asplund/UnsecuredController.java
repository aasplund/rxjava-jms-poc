package se.asplund;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.ws.rs.container.AsyncResponse;
import java.util.Date;
import java.util.Random;

public abstract class UnsecuredController extends AbstractAsyncController {
	private static final Logger logger = LoggerFactory.getLogger(UnsecuredController.class);
	private static final Random random = new Random(new Date().getTime());

	@Override
	protected Observable<ApiMessage> createObservable() {
		logger.debug("Unsecured request received");
		return Observable.just(new ApiMessage().withSecurityContext("Anonymous"));
	}

}
