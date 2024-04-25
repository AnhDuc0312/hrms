package com.hrms.sys.exceptions;

public class ServiceUnavailableException extends Exception{
//    Khi một dịch vụ bên ngoài mà hệ thống của bạn phụ thuộc vào không khả dụng.
    public ServiceUnavailableException(String message) {super(message);}
}
