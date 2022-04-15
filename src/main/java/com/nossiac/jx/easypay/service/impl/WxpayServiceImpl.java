package com.nossiac.jx.easypay.service.impl;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.nossiac.jx.easypay.domain.*;
import com.nossiac.jx.easypay.service.WxpayService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class WxpayServiceImpl implements WxpayService {
    private WxpayConfig wxpayConfig;
    private WXPay wxPay = null;

    public WxpayServiceImpl(WxpayConfig wxpayConfig) {
        try {
            this.wxPay = new WXPay(wxpayConfig);
            this.wxpayConfig = wxpayConfig;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WxpayConfig getWxpayConfig() {
        return wxpayConfig;
    }

    public void setWxpayConfig(WxpayConfig wxpayConfig) {
        this.wxpayConfig = wxpayConfig;
    }

    /**
     * 金额转换
     *
     * @param amount
     * @return
     */
    private int convertAmount(Float amount) {
        float totalFloatFee = amount * 100;
        int totalIntegerFee = (int) totalFloatFee;
        return totalIntegerFee;
    }

    public Map<String, String> createPayRequestData(WxpayRequest wxpayRequest) {
        Map<String, String> data = new HashMap<String, String>();

        int totalFee = convertAmount(wxpayRequest.getTotalFee());
        data.put("body", wxpayRequest.getBody());
        data.put("out_trade_no", wxpayRequest.getOutTradeNo());
        data.put("device_info", wxpayRequest.getDeviceInfo());
        data.put("fee_type", wxpayRequest.getFeeType());
        data.put("total_fee", String.valueOf(totalFee));
        data.put("attach", wxpayRequest.getAttach());
        data.put("time_start", secondsToTimeExpire(0));
        data.put("time_expire", wxpayRequest.getTimeExpire());
        //data.put("spbill_create_ip", wxpayRequest.getSpbillCreateIp());

        if (!wxpayRequest.getNotifyUrl().isEmpty()) {
            data.put("notify_url", wxpayRequest.getNotifyUrl());
        } else {
            data.put("notify_url", wxpayConfig.getNotifyUrl());
        }

        return data;
    }


    /**
     * 扫码支付
     *
     * @param wxpayRequest
     * @return
     */
    public Map<String, String> nativePay(WxpayRequest wxpayRequest) throws Exception {
        Map<String, String> data = createPayRequestData(wxpayRequest);
        data.put("trade_type", "NATIVE");  // 此处指定为扫码支付
        data.put("product_id", wxpayRequest.getProductId());
        return wxPay.unifiedOrder(data);
    }

    /**
     * APP支付
     *
     * @param wxpayRequest
     * @return
     */
    public Map<String, String> appPay(WxpayRequest wxpayRequest) throws Exception {
        Map<String, String> data = createPayRequestData(wxpayRequest);
        data.put("trade_type", "APP");
        return wxPay.unifiedOrder(data);
    }

    /**
     * JSAPI支付
     *
     * @param wxpayRequest
     * @return
     */
    public Map<String, String> jsApiPay(WxpayRequest wxpayRequest) throws Exception {
        Map<String, String> data = createPayRequestData(wxpayRequest);
        data.put("trade_type", "JSAPI");
        data.put("openid", wxpayRequest.getOpenid());
        return wxPay.unifiedOrder(data);
    }


    /**
     * H5支付
     *
     * @param wxpayRequest
     * @return
     */
    public Map<String, String> mwebPay(WxpayRequest wxpayRequest) throws Exception {
        Map<String, String> data = createPayRequestData(wxpayRequest);
        data.put("trade_type", "MWEB");
        return wxPay.unifiedOrder(data);
    }

    /**
     * 通知
     *
     * @param
     * @return
     */
    public WxpayNotify notify(String notifyData) throws Exception {


        Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyData);  // 转换成map

        if (wxPay.isPayResultNotifySignatureValid(notifyMap)) {
            // 签名正确
            // 进行处理。
            // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功

            WxpayNotify wxpayNotify = new WxpayNotify();
            wxpayNotify.setAppId(notifyMap.get("appid"));
            wxpayNotify.setMchId(notifyMap.get("mch_id"));
            wxpayNotify.setNonceStr(notifyMap.get("nonce_str"));
            wxpayNotify.setSign(notifyMap.get("sign"));
            wxpayNotify.setSignType(notifyMap.get("sign_type"));
            wxpayNotify.setResultCode(notifyMap.get("result_code"));
            wxpayNotify.setErrCode(notifyMap.get("err_code"));
            wxpayNotify.setErrCodeDes(notifyMap.get("err_code_des"));
            wxpayNotify.setOpenId(notifyMap.get("openid"));
            wxpayNotify.setIsSubscribe(notifyMap.get("is_subscribe"));
            wxpayNotify.setTradeType(notifyMap.get("trade_type"));
            wxpayNotify.setBankType(notifyMap.get("bank_type"));
            wxpayNotify.setTotalFee(Float.parseFloat(notifyMap.get("total_fee").toString()));
            wxpayNotify.setCashFee(Float.parseFloat(notifyMap.get("cash_fee").toString()));
            wxpayNotify.setTransactionId(notifyMap.get("transaction_id"));
            wxpayNotify.setOutTradeNo(notifyMap.get("out_trade_no"));
            wxpayNotify.setTimeEnd(notifyMap.get("time_end"));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //加上时间
            Date payDatetime = simpleDateFormat.parse(notifyMap.get("time_end"));
            wxpayNotify.setPayDatetime(simpleDateFormat2.format(payDatetime));//时间格式转换
            return wxpayNotify;
        } else {
            // 签名错误，如果数据里没有sign字段，也认为是签名错误
            return null;
        }
    }


    public Map<String, String> refund(WxpayRefund wxpayRefund) throws Exception {
        Map<String, String> data = new HashMap<>();

        if (!wxpayRefund.getTransactionId().isEmpty()) {
            data.put("transaction_id", wxpayRefund.getTransactionId());
        } else {
            data.put("out_trade_no", wxpayRefund.getOutTradeNo());
        }

        data.put("out_refund_no", wxpayRefund.getOutRefundNo());
        data.put("total_fee", String.valueOf(convertAmount(wxpayRefund.getTotalFee())));
        data.put("refund_fee", String.valueOf(convertAmount(wxpayRefund.getRefundFee())));
        data.put("total_fee", String.valueOf(convertAmount(wxpayRefund.getTotalFee())));

        if (!wxpayRefund.getRefundDesc().isEmpty()) {
            data.put("refund_desc", wxpayRefund.getRefundDesc());
        }

        return wxPay.refund(data);
    }

    public Map<String, String> orderQuery(WxpayQuery wxpayQuery) throws Exception {
        Map<String, String> data = new HashMap<>();

        if (!wxpayQuery.getTransactionId().isEmpty()) {
            data.put("transaction_id", wxpayQuery.getTransactionId());
        } else {
            data.put("out_trade_no", wxpayQuery.getOutTradeNo());
        }

        return wxPay.orderQuery(data);
    }


    public Map<String, String> refundQuery(WxpayQuery wxpayQuery) throws Exception {
        Map<String, String> data = new HashMap<>();

        if (!wxpayQuery.getRefundId().isEmpty()) {
            data.put("refund_id", wxpayQuery.getRefundId());
        } else if (!wxpayQuery.getOutRefundNo().isEmpty()) {
            data.put("out_refund_no", wxpayQuery.getOutRefundNo());
        } else if (!wxpayQuery.getTransactionId().isEmpty()) {
            data.put("transaction_id", wxpayQuery.getTransactionId());
        } else if (!wxpayQuery.getOutTradeNo().isEmpty()) {
            data.put("out_trade_no", wxpayQuery.getOutTradeNo());
        }

        return wxPay.refundQuery(data);
    }

    public String secondsToTimeExpire(int timeout) {
        Date date = new Date();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);

        gc.add(GregorianCalendar.SECOND, timeout);
        date = gc.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String res = sdf.format(date);
        return res;
    }
}
