package com.mash.kratos.config;

import com.mash.kratos.exception.JmsErrorHandler;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;


@Configuration
@EnableJms
public class JmsConfig {
  public static final String VOUCHER_QUEUE = "voucherQueue";

  private final ArtemisProperties artemisProperties;
  private final JmsErrorHandler jmsErrorHandler;

  public JmsConfig(
    ArtemisProperties artemisProperties,
    JmsErrorHandler jmsErrorHandler
  ) {
    this.artemisProperties = artemisProperties;
    this.jmsErrorHandler = jmsErrorHandler;
  }

  /**
   * Configure ActiveMQ Artemis Connection Factory
   */
  @Bean
  public ConnectionFactory connectionFactory() {
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
    try {
      connectionFactory.setBrokerURL(artemisProperties.getBrokerUrl());
      connectionFactory.setUser(artemisProperties.getUser());
      connectionFactory.setPassword(artemisProperties.getPassword());
    } catch (Exception e) {
      throw new RuntimeException("Failed to configure Artemis connection factory", e);
    }
    return connectionFactory;
  }

  /**
   * Configure JMS Template for sending messages
   */
  @Bean
  public JmsTemplate jmsTemplate() {
    JmsTemplate template = new JmsTemplate();
    template.setConnectionFactory(connectionFactory());
    template.setMessageConverter(jacksonJmsMessageConverter());
    template.setDefaultDestinationName(VOUCHER_QUEUE);
    template.setPubSubDomain(false); // Use queues, not topics
    return template;
  }

  /**
   * Configure JMS Listener Container Factory for receiving messages
   */
  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory());
    factory.setMessageConverter(jacksonJmsMessageConverter());
    factory.setConcurrency("1-5");
    factory.setPubSubDomain(false); // Use queues, not topics
    factory.setErrorHandler(jmsErrorHandler);
    return factory;
  }

  /**
   * Configure Jackson Message Converter for JSON serialization
   */
  @Bean
  public MessageConverter jacksonJmsMessageConverter() {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    return converter;
  }
}

