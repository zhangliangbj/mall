package com.mmall.common;

public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;

    ResponseCode(int code,String desc){
        this.code = code ;
        this.desc = desc;
    }

    //把code和desc开放出去
    public int getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }
}
