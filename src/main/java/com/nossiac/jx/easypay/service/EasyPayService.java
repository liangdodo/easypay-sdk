package com.nossiac.jx.easypay.service;

import com.nossiac.jx.easypay.domain.*;
import com.nossiac.jx.easypay.enums.EasyPayPlatformEnum;
import com.nossiac.jx.easypay.enums.EasyPayTypeEnum;
import com.nossiac.jx.easypay.exception.EasyPayException;

import java.util.Map;

public interface EasyPayService {
    public void setAlipayConfig(AlipayConfig alipayConfig);

    public void setWxpayConfig(WxpayConfig wxpayConfig);

    AlipayService getAlipayService();

    public WxpayService getWxpayService();

    public EasyPayRequest getEasyPayRequest();

    public void setEasyPayRequest(EasyPayRequest easyPayRequest);

    public EasyPayTypeEnum getEasyPayType();

    public void setEasyPayType(EasyPayTypeEnum easyPayType);

    public EasyPayPlatformEnum getEasyPayPlatform();

    public void setEasyPayPlatform(EasyPayPlatformEnum easyPayPlatform);

    public Object pay();//支付

    public EasyPayNotify notify(Map<String, String> notifyMap);//以map方式接收的通知

    public EasyPayNotify notify(String notifyData); //以string方式接收通知

    public EasyRefundResponse refund(EasyRefundRequest easyRefundRequest) throws EasyPayException;

    public EasyQueryResponse orderQuery(EasyQueryRequest easyQueryRequest) throws EasyPayException;

    public EasyQueryResponse refundQuery(EasyQueryRequest easyQueryRequest) throws EasyPayException;
}
