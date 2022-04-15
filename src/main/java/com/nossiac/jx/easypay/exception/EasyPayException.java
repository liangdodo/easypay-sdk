package com.nossiac.jx.easypay.exception;

import com.nossiac.jx.easypay.enums.EasyPayErrorCodeEnum;

public class EasyPayException extends RuntimeException {
    private EasyPayErrorCodeEnum errorCode;

    public EasyPayException(final EasyPayErrorCodeEnum easyPayErrorCodeEnum) {
        super(easyPayErrorCodeEnum.getDescription());
        this.errorCode = easyPayErrorCodeEnum;
    }

    public EasyPayException(final String detailMessage) {
        super(detailMessage);
        this.errorCode = EasyPayErrorCodeEnum.UNKNOW_ERROR;
    }

    public EasyPayErrorCodeEnum getErrorCode() {
        return errorCode;
    }
}
