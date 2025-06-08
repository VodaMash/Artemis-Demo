package com.mash.kratos.entity;

import com.fasterxml.jackson.annotation.JsonValue;


public enum VoucherStatus {
  ACTIVE("ACTIVE"),
  REDEEMED("REDEEMED"),
  EXPIRED("EXPIRED");

  private final String value;

  VoucherStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
