package com.hrms.sys.exceptions;

public class NotFoundException extends Exception{
//    Nếu một tài nguyên như người dùng, bộ phận, hoặc hồ sơ không được tìm thấy trong cơ sở dữ liệu.
    public NotFoundException(String message) {super(message);}
}
