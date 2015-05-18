package se.asplund;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.Subscriber;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AsyncMqClient<T> {

	private static final Logger logger = LoggerFactory.getLogger(AsyncMqClient.class);

	private static Map<String, Subscriber> subscribers = new ConcurrentHashMap<>();

	@Autowired
	private JmsTemplate jmsTemplate;

	public Observable<T> sendAsynchronous(T messageBody) {
		logger.debug("SendAsynchronous");

		String uuid = UUID.randomUUID().toString();
		Observable<T> observable = Observable.create(aSubscriber -> subscribers.putIfAbsent(uuid, aSubscriber));

		jmsTemplate.send("mailbox-producer", session ->
				session.createObjectMessage(new JmsMessage<>(uuid, messageBody)));

		return observable;
	}

	@SuppressWarnings("unchecked")
	@JmsListener(destination = "mailbox-client", concurrency = "10" )
	private void receiveMessage(JmsMessage jmsMessage) {
		logger.debug("Receive Message - Client");

		Subscriber subscriber = subscribers.remove(jmsMessage.getId());

		if (subscriber != null && !subscriber.isUnsubscribed()) {
			subscriber.onNext(jmsMessage.getMessage());
			subscriber.onCompleted();
		}
	}

}