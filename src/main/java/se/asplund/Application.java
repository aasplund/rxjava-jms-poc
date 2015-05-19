package se.asplund;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@SuppressWarnings("unchecked")
@ComponentScan
@EnableAutoConfiguration
@EnableJms
public class Application extends ResourceConfig {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public Application() {
		register(AsyncController.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}
