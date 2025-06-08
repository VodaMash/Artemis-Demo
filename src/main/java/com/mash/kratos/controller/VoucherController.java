package com.mash.kratos.controller;

import com.mash.kratos.entity.Voucher;
import com.mash.kratos.service.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/vouchers")
public class VoucherController {
  private static final Logger logger = LoggerFactory.getLogger(VoucherController.class);

  private final VoucherService voucherService;

  public VoucherController(VoucherService voucherService) {
    this.voucherService = voucherService;
  }

  @GetMapping
  public ResponseEntity<Iterable<Voucher>> getAllVouchers() {
    logger.info("Received request to get all vouchers");

    Iterable<Voucher> vouchers = voucherService.getAllVouchers();

    return ResponseEntity.ok(vouchers);
  }

  @GetMapping("/{voucherCode}")
  public ResponseEntity<Voucher> getVoucher(
    @PathVariable String voucherCode
  ) {
    logger.info("Received request to get voucher with code: {}", voucherCode);

    Voucher voucher = voucherService.getVoucherByCode(voucherCode);

    return ResponseEntity.ok(voucher);
  }


  @PostMapping("/create")
  public ResponseEntity<String> createVoucher(
    @RequestParam String voucherCode,
    @RequestParam String description,
    @RequestParam Double amount
  ) {
    logger.info("Received request to create voucher with code: {}", voucherCode);

    voucherService.createVoucherAsync(voucherCode, description, amount);

    return ResponseEntity.ok("Voucher creation initiated for code: " + voucherCode);
  }

  @PostMapping("/redeem")
  public ResponseEntity<String> redeemVoucher(
    @RequestParam String voucherCode
  ) {
    logger.info("Received request to redeem voucher with code: {}", voucherCode);

    voucherService.redeemVoucherAsync(voucherCode);

    return ResponseEntity.ok("Voucher redemption initiated for code: " + voucherCode);
  }

  @PostMapping("/expire")
  public ResponseEntity<String> expireVoucher(
    @RequestParam String voucherCode
  ) {
    logger.info("Received request to expire voucher with code: {}", voucherCode);

    voucherService.expireVoucherAsync(voucherCode);

    return ResponseEntity.ok("Voucher expiration initiated for code: " + voucherCode);
  }
}
