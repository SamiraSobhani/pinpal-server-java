package com.pinpal.pinpalproject.payload;

import lombok.Data;

@Data
public class MessageRequest {

    private String content;
    private Long posterId;
    private Long inReplyToMessageId;
}
