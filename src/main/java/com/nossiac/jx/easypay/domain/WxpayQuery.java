package com.nossiac.jx.easypay.domain;

import lombok.Data;

@Data
public class WxpayQuery {
    private String outTradeNo = "";
    private String transactionId = "";
    private String outRefundNo = "";
    private String refundId = "";
}
