package com.github.shoppingmall.shopping_mall.service.exceptions;

public class RecordExistsException extends RuntimeException {
    public RecordExistsException(String message) {
        super(message);
    }

//    public RecordExistsException(String message) {
//        super("해당  " +message+ " 로 가입된 계정이 존재합니다.");
//    }
}
