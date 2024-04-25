package com.hrms.sys.exceptions;

public class ConflictException extends Exception{
//    Khi có xung đột giữa yêu cầu và trạng thái hiện tại của hệ thống
    public ConflictException(String message) {super(message);}
}
