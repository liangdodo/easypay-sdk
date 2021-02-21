package com.nossiac.jx.easypay.domain;

import com.github.wxpay.sdk.WXPayConstants;
import lombok.Data;

@Data
public class WxpayRequest {
    private String body="";
    private String outTradeNo="";
    private String deviceInfo="";
    private String feeType ="CNY";
    private float totalFee=0.0F;
    private String spbillCreateIp="";
    private String notifyUrl="";
    private String tradeType="";
    private String productId="";
    private String openid="";
    //private String signType;
    private WXPayConstants.SignType signType=WXPayConstants.SignType.HMACSHA256;
    private String attach="";
}
