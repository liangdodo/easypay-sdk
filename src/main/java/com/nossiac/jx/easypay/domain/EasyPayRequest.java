package com.nossiac.jx.easypay.domain;
import lombok.Data;

@Data
public class EasyPayRequest {
    private String tradeNo="";
    private String subject="";
    private float  amount=0.00F;
    private String body="";
    private String openid="";
    private String productId="";
    private String notifyUrl="";
    private String returnUrl="";
    private int timeout=600;//超时时间，单位（秒）
}
