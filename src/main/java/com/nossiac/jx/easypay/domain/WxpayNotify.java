package com.nossiac.jx.easypay.domain;

import lombok.Data;

@Data
public class WxpayNotify {
    private String returnCode="";
    private String returnMsg="";
    private String appId="";
    private String mchId="";
    private String nonceStr="";
    private String sign="";
    private String signType="";
    private String resultCode="";
    private String errCode="";
    private String errCodeDes="";
    private String openId="";
    private String isSubscribe="";
    private String tradeType="";
    private String bankType="";
    private float  totalFee=0.00F;
    private float  cashFee=0.00F;
    private String transactionId="";//微信支付订单号
    private String outTradeNo="";//商户订单号
    private String timeEnd="";//支付完成时间
    private String payDatetime="";//付款时间
}
