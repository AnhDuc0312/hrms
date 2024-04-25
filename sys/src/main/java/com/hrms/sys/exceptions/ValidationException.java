package com.hrms.sys.exceptions;

public class ValidationException extends Exception{
//    Khi dữ liệu không đáp ứng các yêu cầu validation, ví dụ như độ dài quá ngắn hoặc quá dài, hoặc không đúng định dạng.
    public ValidationException (String message) {super(message);}
}
