package com.fast.campus.simplesns.service;

import com.fast.campus.simplesns.exception.ErrorCode;
import com.fast.campus.simplesns.exception.SimpleSnsApplicationException;
import com.fast.campus.simplesns.model.AlarmEvent;
import com.fast.campus.simplesns.model.entity.AlarmEntity;
import com.fast.campus.simplesns.model.entity.UserEntity;
import com.fast.campus.simplesns.repository.AlarmEntityRepository;
import com.fast.campus.simplesns.repository.EmitterRepository;
import com.fast.campus.simplesns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String EVENT_NAME = "alarm";
    private final EmitterRepository emitterRepository;
    private final UserEntityRepository userEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;

    public void send(AlarmEvent event) {

        UserEntity userEntity = userEntityRepository.findById(event.getReceiveUserId()).orElseThrow(() ->
                new SimpleSnsApplicationException(ErrorCode.USER_NOT_FOUND));

        AlarmEntity alarm = alarmEntityRepository.save(AlarmEntity.of(userEntity, event.getAlarmType(), event.getArgs()));

        emitterRepository.getEmitter(event.getReceiveUserId()).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(alarm.getId().toString()).name(EVENT_NAME).data("new alarm"));
            } catch (IOException e) {
                emitterRepository.delete(event.getReceiveUserId());
                throw new SimpleSnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter found"));
    }

    public SseEmitter connectAlarm(Integer userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.save(userId, sseEmitter);
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));
        try {
            sseEmitter.send(SseEmitter.event().id("").name(EVENT_NAME).data("connect completed"));
        } catch (IOException e) {
            throw new SimpleSnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }

}
