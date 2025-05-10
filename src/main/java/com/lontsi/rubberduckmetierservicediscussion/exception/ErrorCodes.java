package com.lontsi.rubberduckmetierservicediscussion.exception;

import lombok.Getter;

@Getter
public enum ErrorCodes {

    // 2000 to 2050 are reserved for the Embedding service Entity Exceptions
    EmbedException(2000),
    // 2050 to 2100 are reserved for the Embedding service Operation Exceptions
    Embedding_Text_Not_Provided(2050), //
    EMBEDDING_CREATION_ERROR(2000),
    // 3000 to 3050 are reserved for the Vector service Entity Exceptions
    VectorException(3000),
    // 3050 to 3100 are reserved for the Vector service Operation Exceptions
    Vector_Save_Exception(3050),
    // 3100 to 3150 are reserved for the Message service Entity Exceptions
    MESSAGE_SAVE_ERROR(4050),
    // 4050 to 4100 are reserved for the Message service Operation Exceptions
    DISCUSSION_CREATION_ERROR(5000),
    DISCUSSION_SAVE_ERROR(5050),

    PRODUCER_CREATION_ERROR(9000),

    // Invalid Entity Exceptions
    PRINCIPAL_NULL(10000),
    ID_USER_NULL(10500);


    private final int code;

    ErrorCodes(int code) {
        this.code = code;
    }


}
