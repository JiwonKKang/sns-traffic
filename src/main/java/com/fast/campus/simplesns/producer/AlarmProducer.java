package com.fast.campus.simplesns.producer;

import com.fast.campus.simplesns.model.AlarmArgs;
import com.fast.campus.simplesns.model.AlarmEvent;
import com.fast.campus.simplesns.model.AlarmType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlarmProducer {

    @Value("${spring.kafka.topic.alarm}")
    private String topic;

    private final KafkaTemplate<Integer, AlarmEvent> kafkaTemplate;

    public void send(AlarmEvent event) {
        kafkaTemplate.send(topic, event.getReceiveUserId(), event);
        log.info("Kafka send complete");
    }

}
