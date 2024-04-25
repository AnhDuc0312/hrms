package com.hrms.sys.exceptions;

public class InvalidDataException extends Exception{
//    Khi dữ liệu đầu vào không hợp lệ hoặc thiếu thông tin cần thiết để thực hiện một hành động.
    public InvalidDataException (String message) {super(message);}
}
