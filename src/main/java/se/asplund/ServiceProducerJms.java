package se.asplund;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

@Component
public class ServiceProducerJms {

	private static final Logger logger = LoggerFactory.getLogger(ServiceProducerJms.class);

	@Autowired
	private JmsTemplate jmsTemplate;

	private Random random = new Random(new Date().getTime());

	@SuppressWarnings("unchecked")
	@JmsListener(destination = "mailbox-producer", concurrency = "10")
	public void receiveMessage(final JmsMessage jmsMessage) {
		logger.debug("Receive Message - Producer");

		try {
			Thread.sleep(random.nextInt(1000) + 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		jmsTemplate.send("mailbox-client", session ->
				session.createObjectMessage(new JmsMessage<>(jmsMessage.getId(), "<" + jmsMessage.getMessage() + ">")));
	}
}
