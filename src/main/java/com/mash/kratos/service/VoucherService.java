package com.mash.kratos.service;

import com.mash.kratos.dto.VoucherAction;
import com.mash.kratos.dto.VoucherMessage;
import com.mash.kratos.entity.Voucher;
import com.mash.kratos.entity.VoucherStatus;
import com.mash.kratos.exception.InvalidVoucherStatusException;
import com.mash.kratos.exception.VoucherAlreadyExistsException;
import com.mash.kratos.exception.VoucherNotFoundException;
import com.mash.kratos.producer.VoucherProducer;
import com.mash.kratos.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class VoucherService {
  private static final Logger logger = LoggerFactory.getLogger(VoucherService.class);

  private final VoucherRepository voucherRepository;
  private final VoucherProducer voucherProducer;

  public VoucherService(
    VoucherRepository voucherRepository,
    VoucherProducer voucherProducer
  ) {
    this.voucherRepository = voucherRepository;
    this.voucherProducer = voucherProducer;
  }

  public List<Voucher> getAllVouchers() {
    logger.info("Fetching all vouchers");

    List<Voucher> vouchers = voucherRepository.findAll();
    if (vouchers.isEmpty())
      logger.warn("No vouchers found");
    else
      logger.info("Found {} vouchers", vouchers.size());

    return vouchers;
  }

  public Voucher getVoucherByCode(String voucherCode) {
    logger.info("Fetching voucher with code: {}", voucherCode);

    Optional<Voucher> optionalVoucher = voucherRepository.findByVoucherCode(voucherCode);
    if (optionalVoucher.isEmpty()) {
      logger.warn("Voucher with code {} not found", voucherCode);
      throw new RuntimeException("Voucher not found");
    }

    Voucher voucher = optionalVoucher.get();
    logger.info("Found voucher: {}", voucher);

    return voucher;
  }

  /**
   * Create a new voucher and send message to queue (used by controller)
   */
  public void createVoucherAsync(
    String voucherCode,
    String description,
    Double amount
  ) {
    logger.info("Initiating async voucher creation for code: {}", voucherCode);

    VoucherMessage voucherMessage = VoucherMessage.builder()
      .voucherCode(voucherCode)
      .description(description)
      .amount(amount)
      .action(VoucherAction.CREATE)
      .build();

    voucherProducer.sendVoucherMessage(voucherMessage);

    logger.info("Voucher creation message sent for code: {}", voucherCode);
  }

  /**
   * Create voucher directly (called by consumer)
   */
  public Voucher createVoucher(VoucherMessage voucherMessage) {
    logger.info("Creating voucher with code: {}", voucherMessage.getVoucherCode());

    if (voucherRepository.existsByVoucherCode(voucherMessage.getVoucherCode())) {
      logger.warn("Voucher with code {} already exists", voucherMessage.getVoucherCode());
      throw new VoucherAlreadyExistsException("Voucher already exists");
    }

    Voucher voucher = Voucher.builder()
      .voucherCode(voucherMessage.getVoucherCode())
      .description(voucherMessage.getDescription())
      .amount(voucherMessage.getAmount())
      .status(VoucherStatus.ACTIVE)
      .build();

    voucherRepository.save(voucher);
    logger.info("Saving voucher: {}", voucher);

    return voucher;
  }


  public void redeemVoucherAsync(String voucherCode) {
    logger.info("Initiating async voucher redemption for code: {}", voucherCode);

    VoucherMessage voucherMessage = VoucherMessage.builder()
      .voucherCode(voucherCode)
      .action(VoucherAction.REDEEM)
      .build();

    voucherProducer.sendVoucherMessage(voucherMessage);

    logger.info("Voucher redemption message sent for code: {}", voucherCode);
  }

  public Voucher redeemVoucher(VoucherMessage voucherMessage) {
    logger.info("Redeeming voucher with code: {}", voucherMessage.getVoucherCode());

    Optional<Voucher> optionalVoucher = voucherRepository.findByVoucherCode(voucherMessage.getVoucherCode());
    if (optionalVoucher.isEmpty()) {
      logger.warn("Voucher with code {} not found for redemption", voucherMessage.getVoucherCode());
      throw new VoucherNotFoundException("Voucher not found");
    }

    Voucher voucher = optionalVoucher.get();
    if (voucher.getStatus() != VoucherStatus.ACTIVE) {
      logger.warn("Voucher with code {} is not active", voucherMessage.getVoucherCode());
      throw new InvalidVoucherStatusException("Voucher is not active");
    }

    voucher.setStatus(VoucherStatus.REDEEMED);
    voucherRepository.save(voucher);
    logger.info("Voucher redeemed successfully: {}", voucher);

    return voucher;
  }

  public void expireVoucherAsync(String voucherCode) {
    logger.info("Initiating async voucher expiration for code: {}", voucherCode);

    VoucherMessage voucherMessage = VoucherMessage.builder()
      .voucherCode(voucherCode)
      .action(VoucherAction.EXPIRE)
      .build();

    voucherProducer.sendVoucherMessage(voucherMessage);

    logger.info("Voucher expiration message sent for code: {}", voucherCode);
  }

  public Voucher expireVoucher(VoucherMessage voucherMessage) {
    logger.info("Expiring voucher with code: {}", voucherMessage.getVoucherCode());

    Optional<Voucher> optionalVoucher = voucherRepository.findByVoucherCode(voucherMessage.getVoucherCode());
    if (optionalVoucher.isEmpty()) {
      logger.warn("Voucher with code {} not found for expiration", voucherMessage.getVoucherCode());
      throw new VoucherNotFoundException("Voucher not found");
    }

    Voucher voucher = optionalVoucher.get();
    if (voucher.getStatus() != VoucherStatus.ACTIVE) {
      logger.warn("Voucher with code {} is not active and cannot be expired", voucherMessage.getVoucherCode());
      throw new InvalidVoucherStatusException("Voucher is not active and cannot be expired");
    }

    voucher.setStatus(VoucherStatus.EXPIRED);
    voucherRepository.save(voucher);
    logger.info("Voucher expired successfully: {}", voucher);

    return voucher;
  }
}
