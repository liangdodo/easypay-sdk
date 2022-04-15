package com.nossiac.jx.easypay.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.nossiac.jx.easypay.domain.*;

import java.util.Map;

public interface AlipayService {
    public AlipayConfig getAlipayConfig();

    public void setAlipayConfig(AlipayConfig alipayConfig);

    public String appPay(AlipayRequest alipayPayRequest) throws AlipayApiException;

    public String pagePay(AlipayRequest alipayPayRequest) throws AlipayApiException;

    public String wapPay(AlipayRequest alipayPayRequest) throws AlipayApiException;

    public AlipayTradePrecreateResponse qrCodePay(AlipayRequest alipayPayRequest) throws AlipayApiException;

    public AlipayNotify notify(Map<String, String> requestParams) throws AlipayApiException;

    public AlipayTradeRefundResponse refund(AlipayRefund alipayRefund) throws AlipayApiException;

    public AlipayTradeQueryResponse orderQuery(AlipayQuery alipayQuery) throws AlipayApiException;

    public AlipayTradeFastpayRefundQueryResponse refundQuery(AlipayQuery alipayQuery) throws AlipayApiException;

    public String secondsToTimeoutExpress(int timeout);
}
