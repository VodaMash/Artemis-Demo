package com.mash.kratos.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;


@Component
public class JmsErrorHandler implements ErrorHandler {
  private static final Logger logger = LoggerFactory.getLogger(JmsErrorHandler.class);

  @Override
  public void handleError(Throwable t) {
    logger.error("Error occurred in JMS listener: {}", t.getMessage(), t);
  }
}