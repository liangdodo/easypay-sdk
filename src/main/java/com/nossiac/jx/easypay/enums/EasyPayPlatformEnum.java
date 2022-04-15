package com.nossiac.jx.easypay.enums;

public enum EasyPayPlatformEnum {
    UNKNOWN("unknown", "未知"),
    ALIPAY("alipay", "支付宝"),
    WXPAY("wxpay", "微信");

    private String code;
    private String name;

    private EasyPayPlatformEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }
}
