package com.nossiac.jx.easypay.domain;

import lombok.Data;

@Data
public class AlipayConfig {
    private String gateWay = "https://openapi.alipay.com/gateway.do";
    private String appId = "";
    private String appPrivateKey = "";
    private String alipayPublicKey = "";
    private String charset = "utf-8";
    private String format = "json";
    private String signType = "RSA2";
    private String notifyUrl = "";
    private String returnUrl = "";
}
