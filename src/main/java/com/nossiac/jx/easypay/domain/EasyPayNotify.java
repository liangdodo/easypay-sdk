package com.nossiac.jx.easypay.domain;

import lombok.Data;

@Data
public class EasyPayNotify {
    private String tradeNo = "";
    private String payTradeNo = "";
    private float amount = 0.00F;
    private String payDatetime = "";

    private AlipayNotify alipayNotify = null;
    private WxpayNotify wxpayNotify = null;
}
