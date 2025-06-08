package com.mash.kratos.repository;

import com.mash.kratos.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface VoucherRepository extends JpaRepository<Voucher, Long> {
  boolean existsByVoucherCode(String voucherCode);

  Optional<Voucher> findByVoucherCode(String voucherCode);
}
