package se.asplund;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AsyncMqClient<T> {

	private static final Logger logger = LoggerFactory.getLogger(AsyncMqClient.class);

	private static Map<String, CompletableFuture> futures = new ConcurrentHashMap<>();

	@Autowired
	private JmsTemplate jmsTemplate;

	public CompletableFuture<T> sendAsynchronous(T messageBody) {
		logger.debug("SendAsynchronous");

		String uuid = UUID.randomUUID().toString();

		CompletableFuture<T> future = new CompletableFuture<>();

		futures.putIfAbsent(uuid, future);

		jmsTemplate.send("mailbox-producer", session ->
				session.createObjectMessage(new JmsMessage<>(uuid, messageBody)));

		return future;
	}

	@SuppressWarnings("unchecked")
	@JmsListener(destination = "mailbox-client", concurrency = "10" )
	private void receiveMessage(JmsMessage jmsMessage) {
		logger.debug("Receive Message - Client: {}", jmsMessage.getMessage());

		CompletableFuture<T> future = futures.remove(jmsMessage.getId());

		if (future != null && !future.isDone()) {
			future.complete((T)jmsMessage.getMessage());
		}
	}

}