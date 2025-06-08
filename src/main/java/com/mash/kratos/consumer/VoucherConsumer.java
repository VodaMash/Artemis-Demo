package com.mash.kratos.consumer;

import com.mash.kratos.dto.VoucherMessage;
import com.mash.kratos.service.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;


@Component
public class VoucherConsumer {
  private static final Logger logger = LoggerFactory.getLogger(VoucherConsumer.class);

  private final VoucherService voucherService;

  public VoucherConsumer(VoucherService voucherService) {
    this.voucherService = voucherService;
  }

  @JmsListener(destination = "voucher.queue")
  public void consumeVoucherMessage(VoucherMessage voucherMessage) {
    logger.info("Received voucher message: {}", voucherMessage);

    switch (voucherMessage.getAction()) {
      case CREATE:
        handleCreateVoucher(voucherMessage);
        break;
      case REDEEM:
        handleRedeemVoucher(voucherMessage);
        break;
      case EXPIRE:
        handleExpireVoucher(voucherMessage);
        break;
      default:
        logger.warn("Unknown action: {}", voucherMessage.getAction());
    }
  }

  private void handleCreateVoucher(VoucherMessage voucherMessage) {
    logger.info("Processing CREATE voucher: {}", voucherMessage.getVoucherCode());

    voucherService.createVoucher(voucherMessage);
  }

  private void handleRedeemVoucher(VoucherMessage voucherMessage) {
    logger.info("Processing REDEEM voucher: {}", voucherMessage.getVoucherCode());

    voucherService.redeemVoucher(voucherMessage);
  }

  private void handleExpireVoucher(VoucherMessage voucherMessage) {
    logger.info("Processing EXPIRE voucher: {}", voucherMessage.getVoucherCode());

    voucherService.expireVoucher(voucherMessage);
  }
}
