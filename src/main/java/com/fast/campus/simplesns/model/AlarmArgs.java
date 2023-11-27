package com.fast.campus.simplesns.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmArgs {

    private Integer fromUserId;
    private Integer targetId;

    public static AlarmArgs of(Integer fromUserId, Integer targetId) {
        return new AlarmArgs(
                fromUserId,
                targetId
        );
    }
}
