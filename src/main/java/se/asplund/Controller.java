package se.asplund;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class Controller {
	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	@Autowired
	private AsyncMqClient<String> mqClient;

	@RequestMapping(value = "/", method = GET, produces = "plain/text")
	@ResponseBody
	public DeferredResult<String> asyncOne() {
		logger.debug("Request received...");
		DeferredResult<String> deferredResult = new DeferredResult<>();

		mqClient.sendAsynchronous("Hello")
				.thenCompose(str -> mqClient.sendAsynchronous(str + " World!"))
				.whenComplete((result, error) -> {
					if (error == null) {
						deferredResult.setResult(result);
					} else {
						deferredResult.setErrorResult(error);
					}
				});

		return deferredResult;
	}

	@RequestMapping(value = "/{count}", method = GET, produces = "plain/text")
	@ResponseBody
	public DeferredResult<String> async(@PathVariable int count) {
		logger.debug("Request received...");
		DeferredResult<String> deferredResult = new DeferredResult<>();

		sequence(Stream.iterate(1, i -> i + 1).limit(count)
				.map(i -> mqClient.sendAsynchronous(i + ". - Hello")
								.thenCompose(str -> mqClient.sendAsynchronous(str + " World!"))
				)
				.collect(Collectors.toList()))
				.whenComplete((result, error) -> {
					if (error == null) {
						deferredResult.setResult(result.stream().collect(Collectors.joining("\n")));
					} else {
						deferredResult.setErrorResult(error);
					}
				});


		return deferredResult;
	}

	private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
		CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		return allDoneFuture.thenApply(v -> joinFutures(futures));
	}

	private static <T> List<T> joinFutures(List<CompletableFuture<T>> futures) {
		return futures.stream().map(CompletableFuture::join).collect(Collectors.<T>toList());
	}
}
