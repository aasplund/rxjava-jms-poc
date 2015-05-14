package se.asplund;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;
import rx.Subscription;

import java.util.stream.Collectors;

@RestController
public class Controller {
	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	@Autowired
	private AsyncMqClient<String> mqClient;

	@RequestMapping("/{count}")
	public DeferredResult<String> home(@PathVariable int count) {
		logger.debug("Request received...");
		DeferredResult<String> deferredResult = new DeferredResult<>();

		final Subscription subscription = Observable.range(1, count)
				.flatMap(i -> mqClient.sendAsynchronous(i + ". - Hello"))
				.map(s -> s.toUpperCase())
				.flatMap(s -> mqClient.sendAsynchronous(s + " World!"))
				.buffer(count)
				.subscribe(v -> deferredResult.setResult(v.stream().collect(Collectors.joining("\n"))));

		deferredResult.onCompletion(subscription::unsubscribe);

		return deferredResult;
	}
}
