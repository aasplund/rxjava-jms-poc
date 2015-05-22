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
public class AsyncMqClient {

	private static final Logger logger = LoggerFactory.getLogger(AsyncMqClient.class);

	private static Map<String, Pair> subscribers = new ConcurrentHashMap<>();

	@Autowired
	private JmsTemplate jmsTemplate;

	public Observable<ApiMessage> sendAsynchronous(ApiMessage apiMessage) {
		logger.debug("SendAsynchronous");

		final String uuid = UUID.randomUUID().toString();

		Observable<ApiMessage> observable = Observable.create(aSubscriber ->
				subscribers.putIfAbsent(uuid, new Pair(aSubscriber, apiMessage)));

		jmsTemplate.send("mailbox-producer", session ->
				session.createObjectMessage(new JmsMessage(uuid, apiMessage.getMessageBody())));

		return observable;
	}

	@SuppressWarnings("unchecked")
	@JmsListener(destination = "mailbox-client", concurrency = "10" )
	private void receiveMessage(JmsMessage jmsMessage) {
		logger.debug("Receive Message - Client");

		Pair pair = subscribers.remove(jmsMessage.getId());

		if (pair != null && !pair.getSubscriber().isUnsubscribed()) {
			pair.getSubscriber().onNext(pair.getApiMessage().withBody(jmsMessage.getMessage().toString()));
			pair.getSubscriber().onCompleted();
		}
	}

	private class Pair {
		private Subscriber<? super ApiMessage> subscriber;
		private ApiMessage apiMessage;

		public Pair(Subscriber<? super ApiMessage> subscriber, ApiMessage apiMessage) {
			this.subscriber = subscriber;
			this.apiMessage = apiMessage;
		}

		public Subscriber<? super ApiMessage> getSubscriber() {
			return subscriber;
		}

		public ApiMessage getApiMessage() {
			return apiMessage;
		}
	}
}