package com.fast.campus.simplesns.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AlarmEvent {

    private Integer receiveUserId;
    private AlarmArgs args;
    private AlarmType alarmType;

    public static AlarmEvent of(Integer receiveUserId, AlarmArgs args, AlarmType alarmType) {
        return new AlarmEvent(
                receiveUserId,
                args,
                alarmType
        );
    }

}
