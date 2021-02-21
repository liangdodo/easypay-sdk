package com.nossiac.jx.easypay.domain;

import lombok.Data;

@Data
public class WxpayRefund {
    private String transactionId = "";//微信支付订单号
    private String outTradeNo = "";//商户订单号(二选一)
    private String outRefundNo = "";//商户退款单号
    private float totalFee = 0.00F;//订单金额
    private float refundFee = 0.00F;//退款金额
    private String refundDesc="";//退款原因
}
