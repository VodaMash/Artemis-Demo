package com.mash.kratos.dto;

import com.fasterxml.jackson.annotation.JsonValue;


public enum VoucherAction {
  CREATE("CREATE"),
  REDEEM("REDEEM"),
  EXPIRE("EXPIRE");

  private final String value;

  VoucherAction(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
