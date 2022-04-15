package com.nossiac.jx.easypay.domain;

import com.github.wxpay.sdk.WXPayConstants;
import lombok.Data;

@Data
public class WxpayJsApi {
    private String appId;
    private String nonceStr;
    private String packAge;
    private String paySign;
    private Long timestamp;
    private WXPayConstants.SignType signType;
}
