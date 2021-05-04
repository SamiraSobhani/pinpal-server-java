package com.pinpal.pinpalproject.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages", indexes = {
        @Index(columnList = "senderId", name = "senderId_idx"),
        @Index(columnList = "inReplyToMessageId", name = "inReplyToMessageId_idx"),
        @Index(columnList = "posterId", name = "posterId_idx"),
        @Index(columnList = "timestamp", name = "timestamp_idx")})
@Getter
@Setter
@RequiredArgsConstructor
public class PosterMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long posterId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = true)
    private String userImage;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private Long inReplyToMessageId = -1L;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Transient
    private List<PosterMessage> nestedReplies = new ArrayList<>();
}
