package com.nossiac.jx.easypay.domain;

import lombok.Data;

@Data
public class AlipayRequest {
    private String subject="";
    private String outTradeNo="";
    private String timeoutExpress="";
    private float  totalAmount=0.00F;
    private String body="";
    private String notifyUrl="";
    private String returnUrl="";
}
