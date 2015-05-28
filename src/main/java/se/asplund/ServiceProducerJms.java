package se.asplund;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.Date;
import java.util.Random;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class ServiceProducerJms {

	private static final Logger logger = LoggerFactory.getLogger(ServiceProducerJms.class);

	@Autowired
	private JmsTemplate jmsTemplate;

	private final Random random = new Random(new Date().getTime());

	@SuppressWarnings("unchecked")
	@JmsListener(destination = "mailbox-producer", concurrency = "10")
	public void receiveMessage(JmsMessage jmsMessage) {
		logger.debug("Receive Message - Producer");

		Observable.timer(random.nextInt(1000) + 1000, MILLISECONDS).toBlocking().first();

		jmsTemplate.send("mailbox-client", session ->
				session.createObjectMessage(new JmsMessage(jmsMessage.getId(), jmsMessage.getMessage().toString().toUpperCase())));
	}
}
