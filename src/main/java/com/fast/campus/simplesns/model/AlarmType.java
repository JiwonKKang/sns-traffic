package com.fast.campus.simplesns.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AlarmType {

    NEW_COMMENT_ON_POST("new commnen!"),
    NEW_LIKE_ON_POST("new like!"),
    ;

    private final String alarmText;
}
