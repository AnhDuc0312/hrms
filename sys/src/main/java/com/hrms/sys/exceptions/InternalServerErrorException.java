package com.hrms.sys.exceptions;

public class InternalServerErrorException extends Exception{
//    Một lỗi nội bộ của máy chủ xảy ra, không thể xác định được nguyên nhân cụ thể.
    public InternalServerErrorException(String message) {super(message);}
}
