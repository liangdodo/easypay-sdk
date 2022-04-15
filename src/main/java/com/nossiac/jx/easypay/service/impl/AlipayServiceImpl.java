package com.nossiac.jx.easypay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.nossiac.jx.easypay.domain.*;
import com.nossiac.jx.easypay.service.AlipayService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AlipayServiceImpl implements AlipayService {
    AlipayClient alipayClient = null;
    private AlipayConfig alipayConfig;

    public AlipayServiceImpl(AlipayConfig alipayConfig) {
        this.alipayClient = new DefaultAlipayClient(
                alipayConfig.getGateWay(),
                alipayConfig.getAppId(),
                alipayConfig.getAppPrivateKey(),
                alipayConfig.getFormat(),
                alipayConfig.getCharset(),
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getSignType());
        this.alipayConfig = alipayConfig;
    }

    public AlipayConfig getAlipayConfig() {
        return alipayConfig;
    }

    public void setAlipayConfig(AlipayConfig alipayConfig) {
        this.alipayConfig = alipayConfig;
    }


    /**
     * APP支付
     *
     * @param
     * @return
     */
    @Override
    public String appPay(AlipayRequest alipayPayRequest) throws AlipayApiException {
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(alipayPayRequest.getBody());
        model.setSubject(alipayPayRequest.getSubject());
        model.setOutTradeNo(alipayPayRequest.getOutTradeNo());
        model.setTimeoutExpress(alipayPayRequest.getTimeoutExpress());
        model.setTotalAmount(String.valueOf(alipayPayRequest.getTotalAmount()));
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);

        if (!alipayPayRequest.getNotifyUrl().isEmpty()) {
            request.setNotifyUrl(alipayPayRequest.getNotifyUrl());
        } else {
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
        }

        //这里和普通的接口调用不同，使用的是sdkExecute
        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
        return response.getBody();
    }

    /**
     * PC网页支付
     *
     * @param
     * @return
     * @throws AlipayApiException
     */
    @Override
    public String pagePay(AlipayRequest alipayPayRequest) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setBody(alipayPayRequest.getBody());
        model.setSubject(alipayPayRequest.getSubject());
        model.setOutTradeNo(alipayPayRequest.getOutTradeNo());
        model.setTimeoutExpress(alipayPayRequest.getTimeoutExpress());
        model.setTotalAmount(String.valueOf(alipayPayRequest.getTotalAmount()));
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        request.setBizModel(model);

        if (!alipayPayRequest.getNotifyUrl().isEmpty()) {
            request.setNotifyUrl(alipayPayRequest.getNotifyUrl());
        } else {
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
        }

        request.setReturnUrl(alipayPayRequest.getReturnUrl());
        return alipayClient.pageExecute(request).getBody();
    }


    /**
     * WAP支付
     *
     * @return
     */
    public String wapPay(AlipayRequest alipayPayRequest) throws AlipayApiException {
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setBody(alipayPayRequest.getBody());
        model.setSubject(alipayPayRequest.getSubject());
        model.setOutTradeNo(alipayPayRequest.getOutTradeNo());
        model.setTimeoutExpress(alipayPayRequest.getTimeoutExpress());
        model.setTotalAmount(String.valueOf(alipayPayRequest.getTotalAmount()));
        model.setProductCode("QUICK_WAP_WAY");
        request.setBizModel(model);
        request.setNotifyUrl(alipayPayRequest.getNotifyUrl());

        if (!alipayPayRequest.getNotifyUrl().isEmpty()) {
            request.setNotifyUrl(alipayPayRequest.getNotifyUrl());
        } else {
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
        }

        request.setReturnUrl(alipayPayRequest.getReturnUrl());
        return alipayClient.pageExecute(request).getBody();
    }

    /**
     * 支付宝二维码支付
     *
     * @param alipayPayRequest
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradePrecreateResponse qrCodePay(AlipayRequest alipayPayRequest) throws AlipayApiException {
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setBody(alipayPayRequest.getBody());
        model.setSubject(alipayPayRequest.getSubject());
        model.setOutTradeNo(alipayPayRequest.getOutTradeNo());
        model.setQrCodeTimeoutExpress(alipayPayRequest.getTimeoutExpress());
        model.setTotalAmount(String.valueOf(alipayPayRequest.getTotalAmount()));
        request.setBizModel(model);

        if (!alipayPayRequest.getNotifyUrl().isEmpty()) {
            request.setNotifyUrl(alipayPayRequest.getNotifyUrl());
        } else {
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
        }

        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        return response;
    }

    public boolean notifyCheck(Map<String, String> requestParams) throws AlipayApiException {
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();

        //Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String valueStr = (String) requestParams.get(name);


            /*String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }*/
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        return AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType());
    }

    //异步通知
    public AlipayNotify notify(Map<String, String> requestParams) throws AlipayApiException {
        AlipayNotify alipayNotify = new AlipayNotify();
        boolean signVerified = notifyCheck(requestParams);

        //验证失败
        if (!signVerified) {
            return null;
        }

        //验证成功,设置通知属性值
        alipayNotify.setOutTradeNo(requestParams.get("out_trade_no"));
        alipayNotify.setTradeNo(requestParams.get("trade_no"));
        alipayNotify.setCharset(requestParams.get("charset"));
        alipayNotify.setMethod(requestParams.get("method"));
        alipayNotify.setSign(requestParams.get("sign"));
        alipayNotify.setSignType(requestParams.get("sign_type"));
        alipayNotify.setTotalAmount(Float.parseFloat(requestParams.get("total_amount").toString()));
        alipayNotify.setVersion(requestParams.get("version"));
        alipayNotify.setTradeStatus(requestParams.get("trade_status"));
        alipayNotify.setPayDatetime(requestParams.get("gmt_payment"));
        return alipayNotify;
    }

    public AlipayTradeRefundResponse refund(AlipayRefund alipayRefund) throws AlipayApiException {

       /* // (必填) 外部订单号，需要退款交易的商户外部订单号
        String outTradeNo = "tradepay14817938139942440181";

        // (必填) 退款金额，该金额必须小于等于订单的支付金额，单位为元
        String refundAmount = "0.01";

        // (可选，需要支持重复退货时必填) 商户退款请求号，相同支付宝交易号下的不同退款请求号对应同一笔交易的不同退款申请，
        // 对于相同支付宝交易号下多笔相同商户退款请求号的退款交易，支付宝只会进行一次退款
        String outRequestNo = "";

        // (必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
        String refundReason = "正常退款，用户买多了";

        // (必填) 商户门店编号，退款情况下可以为商家后台提供退款权限判定和统计等作用，详询支付宝技术支持
        String storeId = "test_store_id";*/

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();

        if (!alipayRefund.getTradeNo().isEmpty()) {
            model.setTradeNo(alipayRefund.getTradeNo());
        } else {
            model.setOutTradeNo(alipayRefund.getOutTradeNo());
        }

        model.setRefundAmount(String.valueOf(alipayRefund.getRefundAmount()));
        model.setOutRequestNo(alipayRefund.getOutRequestNo());

        if (!alipayRefund.getRefundReason().isEmpty()) {
            model.setRefundReason(alipayRefund.getRefundReason());
        }

        request.setBizModel(model);
        return alipayClient.execute(request);
    }

    public AlipayTradeQueryResponse orderQuery(AlipayQuery alipayQuery) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();

        if (!alipayQuery.getTradeNo().isEmpty()) {
            model.setTradeNo(alipayQuery.getTradeNo());
        } else {
            model.setOutTradeNo(alipayQuery.getOutTradeNo());
        }

        request.setBizModel(model);
        return alipayClient.execute(request);
    }


    public AlipayTradeFastpayRefundQueryResponse refundQuery(AlipayQuery alipayQuery) throws AlipayApiException {
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();

        if (!alipayQuery.getTradeNo().isEmpty()) {
            model.setTradeNo(alipayQuery.getTradeNo());
        } else {
            model.setOutTradeNo(alipayQuery.getOutTradeNo());
        }

        model.setOutRequestNo(alipayQuery.getOutRequestNo());
        request.setBizModel(model);
        return alipayClient.execute(request);
    }

    public String secondsToTimeoutExpress(int timeout) {
        int day = (int) (timeout / 3600 / 24);
        int hours = (int) ((timeout - day * 3600 * 24) / 3600);
        int minute = (int) ((timeout - day * 3600 * 24 - hours * 3600) / 60);
        //int seconds = (long)(timeout-day*3600*24-hours*3600-minute*60);

        if (0 == timeout || timeout < 60) {
            return "1c";
        }

        if (day > 15) {
            day = 15;
        }

        if (day > 0) {
            return day + "d";
        }

        if (hours > 0) {
            return hours + "h";
        }

        if (minute > 0) {
            return minute + "m";
        }

        return "";
    }
}
