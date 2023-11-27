package com.fast.campus.simplesns.consumer;

import com.fast.campus.simplesns.model.AlarmEvent;
import com.fast.campus.simplesns.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlarmConsumer {

    private final AlarmService alarmService;

    @KafkaListener(topics = "${spring.kafka.topic.alarm}")
    public void consume(AlarmEvent event, Acknowledgment ack) {
        log.info("kafka consume event {}", event);
        alarmService.send(event);
        ack.acknowledge();
    }
}
