package se.asplund;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.ws.rs.Path;
import java.util.Date;
import java.util.Random;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Path("/authorize")
public abstract class AuthorizedController extends AbstractAsyncController {
	private static final Logger logger = LoggerFactory.getLogger(AuthorizedController.class);
	private static final Random random = new Random(new Date().getTime());

	@Override
	protected Observable<ApiMessage> createObservable() {
		logger.debug("Secured request received");
		return authorize().map(securityContext -> new ApiMessage().withSecurityContext(securityContext));
	}

	private Observable<String> authorize() {
		return Observable.create(aSubscriber ->
				new Thread(() -> {
					Observable.timer(500, MILLISECONDS).toBlocking().first();
					if (random.nextBoolean()) {
						aSubscriber.onNext("Anders");
						aSubscriber.onCompleted();
					} else {
						logger.error("Unauthorized");
						aSubscriber.onError(new SecurityException("Not Authorized"));
					}
				}).start()
		);
	}


}
