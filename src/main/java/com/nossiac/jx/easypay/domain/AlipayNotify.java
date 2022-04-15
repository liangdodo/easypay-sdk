package com.nossiac.jx.easypay.domain;

import lombok.Data;

@Data
public class AlipayNotify {
    private String outTradeNo = "";
    private String tradeNo = "";
    private String method = "";
    private float totalAmount = 0.00F;
    private String sign = "";
    private String version = "";
    private String signType = "";
    private String charset = "";
    private String tradeStatus = "";
    private String payDatetime = "";//付款时间
}
