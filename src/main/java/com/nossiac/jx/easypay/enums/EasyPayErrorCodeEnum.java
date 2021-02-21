package com.nossiac.jx.easypay.enums;

public enum EasyPayErrorCodeEnum {
    UNKNOW_ERROR("10001", "未知异常"),
    SUCCESS("10002", "成功"),
    PARAM_ERROR("10003", "参数错误"),
    CONFIG_ERROR("10004", "配置错误, 请检查是否漏了配置项"),
    ALIPAY_NOTIFY_ID_VERIFY_FAIL("40001", "【支付宝web端支付验证签名】验证notifyId失败"),
    ALIPAY_ASYNC_SIGN_VERIFY_FAIL("40002", "【支付宝web端支付同步返回验证签名】验证签名失败"),
    SYNC_SIGN_VERIFY_FAIL("40003", "同步返回签名失败"),
    ASYNC_SIGN_VERIFY_FAIL("40004", "异步返回签名失败"),
    PAY_TYPE_ERROR("40005", "错误的支付方式"),
    ALIPAY_TRADE_STATUS_IS_NOT_SUCCESS("40006", "支付宝交易状态不是成功"),
    ALIPAY_TIME_FORMAT_ERROR("40007", "支付宝返回的时间格式不对");

    private String code;
    private String description;

    private EasyPayErrorCodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }
}
