package com.nossiac.jx.easypay.domain;

import lombok.Data;

@Data
public class EasyRefundRequest {
    private String tradeNo = "";
    private String payTradeNo = "";
    private String refundNo = "";
    private float refundAmount = 0.00F;
    private float totalAmount = 0.00F;
    private String refundCause = "";
}
