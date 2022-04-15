package com.nossiac.jx.easypay.domain;

import lombok.Data;

@Data
public class EasyQueryResponse {
    private int code = -1;
    private String message = "";
    private Object data;
}
