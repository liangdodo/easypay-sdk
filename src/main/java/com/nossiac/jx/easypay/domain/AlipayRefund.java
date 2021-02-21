package com.nossiac.jx.easypay.domain;

import lombok.Data;

@Data
public class AlipayRefund {
    private String outTradeNo = "";
    private String tradeNo="";
    private float refundAmount = 0.00F;
    private String outRequestNo = "";
    private String refundReason = "";
    //String storeId = "";
}
