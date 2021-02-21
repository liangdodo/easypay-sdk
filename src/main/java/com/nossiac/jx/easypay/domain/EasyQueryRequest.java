package com.nossiac.jx.easypay.domain;

import lombok.Data;

@Data
public class EasyQueryRequest {
    private String tradeNo="";
    private String refundNo="";
    private String payTradeNo="";
    private String payRefundNo="";
}
