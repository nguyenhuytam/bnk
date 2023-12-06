package com.example.demo_test.dto.response;

import lombok.Data;

@Data
public class BaseResponse {
    private  Object data;
    private String errorCode = null;
    private String errorDesc = null;
    private  Object errorData;

}
