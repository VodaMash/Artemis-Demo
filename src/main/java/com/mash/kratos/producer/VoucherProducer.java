package com.mash.kratos.producer;

import com.mash.kratos.dto.VoucherMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;


@Component
public class VoucherProducer {
  public static final Logger logger = LoggerFactory.getLogger(VoucherProducer.class);

  private final JmsTemplate jmsTemplate;

  public VoucherProducer(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  /**
   * Send voucher message to the queue (used by service)
   */
  public void sendVoucherMessage(VoucherMessage voucherMessage) {
    logger.info("Sending voucher message to queue: {}", voucherMessage);

    jmsTemplate.convertAndSend("voucher.queue", voucherMessage);

    logger.info("Successfully sent voucher message with code: {}", voucherMessage.getVoucherCode());
  }
}
