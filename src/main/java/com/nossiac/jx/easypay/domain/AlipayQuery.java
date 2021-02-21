package com.nossiac.jx.easypay.domain;

import lombok.Data;

@Data
public class AlipayQuery {
    private String outTradeNo="";
    private String tradeNo="";
    private String outRequestNo="";
}
