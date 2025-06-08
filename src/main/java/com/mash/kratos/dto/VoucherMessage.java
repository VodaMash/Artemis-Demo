package com.mash.kratos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherMessage {
  @JsonProperty("voucherCode")
  private String voucherCode;

  @JsonProperty("description")
  private String description;

  @JsonProperty("amount")
  private Double amount;

  @JsonProperty("action")
  private VoucherAction action; // CREATE, UPDATE, DELETE
}

