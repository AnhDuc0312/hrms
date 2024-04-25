package com.hrms.sys.exceptions;

public class UnauthorizedException extends Exception{
//    Khi người dùng không có quyền truy cập vào một tài nguyên hoặc thực hiện một hành động cụ thể.
    public UnauthorizedException(String message) {super(message);}
}
