package com.nossiac.jx.easypay.enums;

import com.nossiac.jx.easypay.exception.EasyPayException;

public enum EasyPayTypeEnum {
    UNKNOWN("unknown", EasyPayPlatformEnum.UNKNOWN, "未知的支付方式"),
    ALIPAY_APP("alipay_app", EasyPayPlatformEnum.ALIPAY, "支付宝app支付"),
    ALIPAY_PAGE("alipay_page", EasyPayPlatformEnum.ALIPAY, "支付宝page支付"),
    ALIPAY_WAP("alipay_wap", EasyPayPlatformEnum.ALIPAY, "支付宝wap支付"),
    ALIPAY_QRCODE("alipay_qrcode", EasyPayPlatformEnum.ALIPAY, "支付宝二维码支付"),

    WXPAY_JSAPI("JSAPI", EasyPayPlatformEnum.WXPAY, "微信公众账号/小程序支付"),
    WXPAY_MWEB("MWEB", EasyPayPlatformEnum.WXPAY, "微信H5支付"),
    WXPAY_NATIVE("NATIVE", EasyPayPlatformEnum.WXPAY, "微信Native支付"),
    WXPAY_APP("APP", EasyPayPlatformEnum.WXPAY, "微信APP支付");

    private String code;
    private EasyPayPlatformEnum platform;
    private String desc;

    private EasyPayTypeEnum(String code, EasyPayPlatformEnum platform, String desc) {
        this.code = code;
        this.platform = platform;
        this.desc = desc;
    }

    public static EasyPayTypeEnum getByName(String code) {
        EasyPayTypeEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            EasyPayTypeEnum bestPayTypeEnum = var1[var3];
            if (bestPayTypeEnum.name().equalsIgnoreCase(code)) {
                return bestPayTypeEnum;
            }
        }

        throw new EasyPayException(EasyPayErrorCodeEnum.PAY_TYPE_ERROR);
    }

    public String getCode() {
        return this.code;
    }

    public EasyPayPlatformEnum getPlatform() {
        return this.platform;
    }

    public String getDesc() {
        return this.desc;
    }
}
