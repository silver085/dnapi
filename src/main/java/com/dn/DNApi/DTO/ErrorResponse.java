package com.dn.DNApi.DTO;

public class ErrorResponse extends  BaseResponse{
    public ErrorResponse(String exception) {
        super(true, exception);
    }
}
