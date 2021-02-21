package com.nossiac.jx.easypay.domain;
import lombok.Data;

@Data
public class EasyRefundResponse {
    private int code=-1;//退款结果代码
    private String message="";//结果消息
    private String tradeNo="";//商户订单号
    private String refundNo="";//商户退单号
    private String payRefundNo="";//支付平台退单号
    private float  refundAmount=0.00F;//退款金额
}
