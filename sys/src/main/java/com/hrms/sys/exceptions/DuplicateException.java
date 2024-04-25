package com.hrms.sys.exceptions;

public class DuplicateException extends Exception{
//    Khi tạo một bản ghi mới mà giá trị của một trường không duy nhất (ví dụ: email, số điện thoại) đã tồn tại trong cơ sở dữ liệu.
    public DuplicateException(String message) {super(message);}
}
