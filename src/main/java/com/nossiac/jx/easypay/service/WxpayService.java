package com.nossiac.jx.easypay.service;

import com.nossiac.jx.easypay.domain.*;

import java.util.Map;

public interface WxpayService {
    public WxpayConfig getWxpayConfig();

    public void setWxpayConfig(WxpayConfig wxpayConfig);

    public Map<String, String> nativePay(WxpayRequest wxpayRequest) throws Exception;

    public Map<String, String> appPay(WxpayRequest wxpayRequest) throws Exception;

    public Map<String, String> jsApiPay(WxpayRequest wxpayRequest) throws Exception;

    public Map<String, String> mwebPay(WxpayRequest wxpayRequest) throws Exception;

    public WxpayNotify notify(String notifyData) throws Exception;

    public Map<String, String> refund(WxpayRefund wxpayRefund) throws Exception;

    public Map<String, String> orderQuery(WxpayQuery wxpayQuery) throws Exception;

    public Map<String, String> refundQuery(WxpayQuery wxpayQuery) throws Exception;

    public String secondsToTimeExpire(int timeout);
}
