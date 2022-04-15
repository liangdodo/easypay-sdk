package com.nossiac.jx.easypay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.github.wxpay.sdk.WXPayUtil;
import com.nossiac.jx.easypay.domain.*;
import com.nossiac.jx.easypay.enums.EasyPayPlatformEnum;
import com.nossiac.jx.easypay.enums.EasyPayTypeEnum;
import com.nossiac.jx.easypay.exception.EasyPayException;
import com.nossiac.jx.easypay.service.AlipayService;
import com.nossiac.jx.easypay.service.EasyPayService;
import com.nossiac.jx.easypay.service.WxpayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class EasyPayServiceImpl implements EasyPayService {
    final Logger logger = LoggerFactory.getLogger(EasyPayServiceImpl.class);
    private AlipayService alipayService;
    private WxpayService wxpayService;
    private EasyPayRequest easyPayRequest;
    private EasyPayTypeEnum easyPayType;
    private EasyPayPlatformEnum easyPayPlatform;

    //设置支付宝配置信息
    public void setAlipayConfig(AlipayConfig alipayConfig) {
        alipayService = new AlipayServiceImpl(alipayConfig);
    }

    public void setWxpayConfig(WxpayConfig wxpayConfig) {
        wxpayService = new WxpayServiceImpl(wxpayConfig);
    }

    public AlipayService getAlipayService() {
        return alipayService;
    }

    public void setAlipayService(AlipayService alipayService) {
        this.alipayService = alipayService;
    }

    public EasyPayRequest getEasyPayRequest() {
        return easyPayRequest;
    }

    public void setEasyPayRequest(EasyPayRequest easyPayRequest) {
        this.easyPayRequest = easyPayRequest;
    }

    public EasyPayTypeEnum getEasyPayType() {
        return easyPayType;
    }

    public void setEasyPayType(EasyPayTypeEnum easyPayType) {
        this.easyPayType = easyPayType;
    }

    public EasyPayPlatformEnum getEasyPayPlatform() {
        return easyPayPlatform;
    }

    public void setEasyPayPlatform(EasyPayPlatformEnum easyPayPlatform) {
        this.easyPayPlatform = easyPayPlatform;
    }

    public WxpayService getWxpayService() {
        return wxpayService;
    }

    public void setWxpayService(WxpayService wxpayService) {
        this.wxpayService = wxpayService;
    }

    public Object pay() throws EasyPayException {

        //微信支付
        if (EasyPayPlatformEnum.WXPAY == easyPayType.getPlatform()) {

            //创建支付宝请求对象
            WxpayRequest wxpayRequest = new WxpayRequest();
            wxpayRequest.setOutTradeNo(easyPayRequest.getTradeNo());
            //wxpayRequest.setSpbillCreateIp("113.114.115.116");
            wxpayRequest.setTotalFee(easyPayRequest.getAmount());
            wxpayRequest.setBody(easyPayRequest.getSubject());
            wxpayRequest.setAttach(easyPayRequest.getBody());
            wxpayRequest.setNotifyUrl(easyPayRequest.getNotifyUrl());
            wxpayRequest.setTimeExpire(wxpayService.secondsToTimeExpire(easyPayRequest.getTimeout()));//设置超时时间

            try {
                //Native支付
                if (easyPayType == EasyPayTypeEnum.WXPAY_NATIVE) {
                    wxpayRequest.setProductId(wxpayRequest.getProductId());
                    return wxpayService.nativePay(wxpayRequest);
                }

                //APP支付
                if (easyPayType == EasyPayTypeEnum.WXPAY_APP) {
                    return wxpayService.appPay(wxpayRequest);
                }

                //公众号/小程序支付
                if (easyPayType == EasyPayTypeEnum.WXPAY_JSAPI) {
                    wxpayRequest.setOpenid(easyPayRequest.getOpenid());
                    Map<String, String> resMap = wxpayService.jsApiPay(wxpayRequest);

                    if (resMap == null) {
                        return null;
                    }

                    WxpayJsApi wxpayJsApi = new WxpayJsApi();
                    wxpayJsApi.setAppId(resMap.get("appid"));
                    wxpayJsApi.setNonceStr(resMap.get("nonce_str"));
                    String packAge = String.format("prepay_id=%s", resMap.get("prepay_id"));
                    wxpayJsApi.setPackAge(packAge);

                    ZoneId zone = ZoneId.systemDefault();
                    LocalDateTime dateTime = LocalDateTime.now();
                    wxpayJsApi.setTimestamp(dateTime.atZone(zone).toInstant().getEpochSecond());
                    wxpayJsApi.setSignType(wxpayRequest.getSignType());

                    Map<String, String> signMap = new HashMap<>();
                    signMap.put("appId", wxpayJsApi.getAppId());
                    signMap.put("timeStamp", String.valueOf(wxpayJsApi.getTimestamp()));
                    signMap.put("nonceStr", wxpayJsApi.getNonceStr());
                    signMap.put("package", wxpayJsApi.getPackAge());
                    signMap.put("signType", wxpayJsApi.getSignType().toString());
                    String paySign = WXPayUtil.generateSignature(signMap, wxpayService.getWxpayConfig().getKey(), wxpayJsApi.getSignType());
                    wxpayJsApi.setPaySign(paySign);
                    return wxpayJsApi;
                }

                //H5支付
                if (easyPayType == EasyPayTypeEnum.WXPAY_MWEB) {
                    return wxpayService.mwebPay(wxpayRequest);
                }

            } catch (Exception e) {
                throw new EasyPayException(e.getMessage());
            }

        } else if (EasyPayPlatformEnum.ALIPAY == easyPayType.getPlatform()) {//支付宝支付

            //创建支付宝支付请求对象
            AlipayRequest alipayRequest = new AlipayRequest();
            alipayRequest.setOutTradeNo(easyPayRequest.getTradeNo());//订单号
            alipayRequest.setTotalAmount(easyPayRequest.getAmount());//金额
            alipayRequest.setSubject(easyPayRequest.getSubject());//标题
            alipayRequest.setTimeoutExpress(alipayService.secondsToTimeoutExpress(easyPayRequest.getTimeout()));//超时时间
            alipayRequest.setBody(easyPayRequest.getBody());//内容

            try {

                //APP支付
                if (easyPayType == EasyPayTypeEnum.ALIPAY_APP) {
                    return alipayService.appPay(alipayRequest);
                }
                //PAGE支付
                if (easyPayType == EasyPayTypeEnum.ALIPAY_PAGE) {
                    alipayRequest.setReturnUrl(easyPayRequest.getReturnUrl());
                    return alipayService.pagePay(alipayRequest);
                }

                //WAP支付
                if (easyPayType == EasyPayTypeEnum.ALIPAY_WAP) {
                    alipayRequest.setReturnUrl(easyPayRequest.getReturnUrl());
                    return alipayService.wapPay(alipayRequest);
                }

                //二维码支付
                if (easyPayType == EasyPayTypeEnum.ALIPAY_QRCODE) {
                    return alipayService.qrCodePay(alipayRequest);
                }

            } catch (AlipayApiException e) {
                throw new EasyPayException(e.getMessage());
            }
        } else {
            throw new EasyPayException("未知的支付方式");
        }

        return null;
    }

    //以Map方式接收的参数
    public EasyPayNotify notify(Map<String, String> notifyData) throws EasyPayException {

        //支付宝支付
        if (EasyPayPlatformEnum.ALIPAY == easyPayPlatform) {//支付宝支付
            try {
                AlipayNotify alipayNotify = alipayService.notify(notifyData);

                //通知成功
                if (alipayNotify != null) {
                    EasyPayNotify easyPayNotify = new EasyPayNotify();
                    easyPayNotify.setAmount(alipayNotify.getTotalAmount());
                    easyPayNotify.setPayTradeNo(alipayNotify.getTradeNo());
                    easyPayNotify.setTradeNo(alipayNotify.getOutTradeNo());
                    easyPayNotify.setPayDatetime(alipayNotify.getPayDatetime());
                    easyPayNotify.setAlipayNotify(alipayNotify);
                    return easyPayNotify;
                }
            } catch (AlipayApiException e) {
                throw new EasyPayException(e.getCause().getMessage());
            }

        }
        return null;
    }

    //以String方式接收的参数
    public EasyPayNotify notify(String notifyData) throws EasyPayException {

        //微信支付
        if (EasyPayPlatformEnum.WXPAY == easyPayPlatform) {
            try {
                WxpayNotify wxpayNotify = wxpayService.notify(notifyData);

                //通知成功
                if (null != wxpayNotify) {
                    EasyPayNotify easyPayNotify = new EasyPayNotify();
                    easyPayNotify.setAmount(wxpayNotify.getTotalFee());
                    easyPayNotify.setPayTradeNo(wxpayNotify.getTransactionId());
                    easyPayNotify.setTradeNo(wxpayNotify.getOutTradeNo());
                    easyPayNotify.setPayDatetime(wxpayNotify.getPayDatetime());
                    easyPayNotify.setWxpayNotify(wxpayNotify);

                    return easyPayNotify;
                }
            } catch (Exception e) {
                throw new EasyPayException(e.getMessage());
            }
        }

        return null;
    }

    public EasyRefundResponse refund(EasyRefundRequest easyRefundRequest) throws EasyPayException {

        EasyRefundResponse easyRefundResponse = new EasyRefundResponse();
        easyRefundResponse.setCode(-1);

        if (easyPayPlatform == EasyPayPlatformEnum.WXPAY) {

            try {
                WxpayRefund wxpayRefund = new WxpayRefund();
                wxpayRefund.setOutTradeNo(easyRefundRequest.getTradeNo());
                wxpayRefund.setTransactionId(easyRefundRequest.getPayTradeNo());
                wxpayRefund.setTotalFee(easyRefundRequest.getTotalAmount());
                wxpayRefund.setOutRefundNo(easyRefundRequest.getRefundNo());
                wxpayRefund.setRefundFee(easyRefundRequest.getRefundAmount());
                wxpayRefund.setRefundDesc(easyRefundRequest.getRefundCause());
                Map<String, String> resMap = wxpayService.refund(wxpayRefund);


                if (resMap != null) {
                    if (resMap.get("return_code").equals("SUCCESS") && resMap.get("result_code").equals("SUCCESS")) {
                        easyRefundResponse.setCode(0);
                        easyRefundResponse.setMessage(resMap.get("err_code_des"));
                        easyRefundResponse.setTradeNo(resMap.get("out_trade_no"));
                        easyRefundResponse.setRefundNo(resMap.get("out_refund_no"));
                        easyRefundResponse.setPayRefundNo(resMap.get("refund_id"));
                        easyRefundResponse.setRefundAmount(Integer.valueOf(resMap.get("refund_fee")) / 100.00F);
                    } else {
                        throw new EasyPayException("err_code=" + resMap.get("err_code") + "," + "err_code_des=" + resMap.get("err_code_des"));
                    }
                }

            } catch (Exception e) {
                throw new EasyPayException(e.getMessage());
            }
        } else if (easyPayPlatform == EasyPayPlatformEnum.ALIPAY) {

            try {

                AlipayRefund alipayRefund = new AlipayRefund();
                alipayRefund.setOutTradeNo(easyRefundRequest.getTradeNo());//商户单号
                alipayRefund.setTradeNo(easyRefundRequest.getPayTradeNo());//支付宝单号
                alipayRefund.setRefundAmount(easyRefundRequest.getRefundAmount());//退款金额
                alipayRefund.setOutRequestNo(easyRefundRequest.getRefundNo());//退款单号
                alipayRefund.setRefundReason(easyRefundRequest.getRefundCause());//退款原因
                AlipayTradeRefundResponse alipayTradeRefundResponse = alipayService.refund(alipayRefund);


                if (alipayTradeRefundResponse.isSuccess()) {
                    easyRefundResponse.setCode(0);
                    easyRefundResponse.setMessage(alipayTradeRefundResponse.getMsg() + "->" + alipayTradeRefundResponse.getSubMsg());
                    //System.out.println(new ObjectMapper().writeValueAsString(alipayResponse));
                } else {
                    throw new EasyPayException("code=" + alipayTradeRefundResponse.getCode() + ",sub_code=" + alipayTradeRefundResponse.getSubCode() +
                            ",msg=" + alipayTradeRefundResponse.getMsg() + ",sub_msg=" + alipayTradeRefundResponse.getSubMsg());
                }

            } catch (AlipayApiException e) {
                throw new EasyPayException(e.getMessage());
            }
        }

        return easyRefundResponse;
    }

    @Override
    public EasyQueryResponse orderQuery(EasyQueryRequest easyQueryRequest) throws EasyPayException {
        EasyQueryResponse easyQueryResponse = new EasyQueryResponse();

        if (easyPayPlatform == EasyPayPlatformEnum.WXPAY) {
            WxpayQuery wxpayQuery = new WxpayQuery();
            wxpayQuery.setOutTradeNo(easyQueryRequest.getTradeNo());
            wxpayQuery.setTransactionId(easyQueryRequest.getPayTradeNo());

            try {
                Map<String, String> responseMap = wxpayService.orderQuery(wxpayQuery);
                String returnCode = responseMap.get("return_code");
                String returnMsg = responseMap.get("return_msg");

                if (null != returnMsg) {
                    easyQueryResponse.setMessage(returnMsg);
                }

                if (null != returnCode && returnCode.equals("SUCCESS")) {
                    easyQueryResponse.setCode(0);
                    easyQueryResponse.setData(responseMap);
                }

            } catch (Exception e) {
                throw new EasyPayException(e.getMessage());
            }

        } else if (easyPayPlatform == EasyPayPlatformEnum.ALIPAY) {
            AlipayQuery alipayQuery = new AlipayQuery();
            alipayQuery.setOutTradeNo(easyQueryRequest.getTradeNo());
            alipayQuery.setTradeNo(easyQueryRequest.getPayTradeNo());

            try {
                AlipayTradeQueryResponse alipayTradeQueryResponse = alipayService.orderQuery(alipayQuery);

                if (alipayTradeQueryResponse.isSuccess()) {
                    easyQueryResponse.setCode(0);
                    easyQueryResponse.setData(alipayTradeQueryResponse);
                }

            } catch (AlipayApiException e) {
                throw new EasyPayException(e.getMessage());
            }
        }

        return easyQueryResponse;
    }

    @Override
    public EasyQueryResponse refundQuery(EasyQueryRequest easyQueryRequest) throws EasyPayException {
        EasyQueryResponse easyQueryResponse = new EasyQueryResponse();

        if (easyPayPlatform == EasyPayPlatformEnum.WXPAY) {

            WxpayQuery wxpayQuery = new WxpayQuery();
            wxpayQuery.setOutTradeNo(easyQueryRequest.getTradeNo());
            wxpayQuery.setTransactionId(easyQueryRequest.getPayTradeNo());
            wxpayQuery.setOutRefundNo(easyQueryRequest.getRefundNo());
            wxpayQuery.setRefundId(easyQueryRequest.getPayRefundNo());

            try {
                Map<String, String> responseMap = wxpayService.refundQuery(wxpayQuery);
                String returnCode = responseMap.get("return_code");
                String returnMsg = responseMap.get("return_msg");

                if (null != returnMsg) {
                    easyQueryResponse.setMessage(returnMsg);
                }

                if (null != returnCode && returnCode.equals("SUCCESS")) {
                    easyQueryResponse.setCode(0);
                    easyQueryResponse.setData(responseMap);
                }

            } catch (Exception e) {
                throw new EasyPayException(e.getMessage());
            }

        } else if (easyPayPlatform == EasyPayPlatformEnum.ALIPAY) {
            AlipayQuery alipayQuery = new AlipayQuery();
            alipayQuery.setOutTradeNo(easyQueryRequest.getTradeNo());
            alipayQuery.setTradeNo(easyQueryRequest.getPayTradeNo());
            alipayQuery.setOutRequestNo(easyQueryRequest.getRefundNo());

            try {
                AlipayTradeFastpayRefundQueryResponse alipayTradeFastpayRefundQueryResponse = alipayService.refundQuery(alipayQuery);

                if (alipayTradeFastpayRefundQueryResponse.isSuccess()) {
                    easyQueryResponse.setCode(0);
                    easyQueryResponse.setData(alipayTradeFastpayRefundQueryResponse);
                }

            } catch (AlipayApiException e) {
                throw new EasyPayException(e.getMessage());
            }
        }

        return easyQueryResponse;
    }
}
