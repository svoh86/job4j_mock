package ru.checkdev.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.SubscribeCategory;
import ru.checkdev.notification.domain.SubscribeTopic;
import ru.checkdev.notification.repository.SubscribeTopicRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@EnableKafka
public class SubscribeTopicService {
    private final SubscribeTopicRepository repository;

    public List<SubscribeTopic> findAll() {
        return repository.findAll();
    }

    @KafkaListener(topics = "site_addSubscribeTopic", groupId = "group-id")
    public SubscribeTopic save(String subscribeTopic) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SubscribeTopic value = objectMapper.readValue(subscribeTopic, SubscribeTopic.class);
        return repository.save(value);
    }

    public List<Integer> findTopicByUserId(int userId) {
        return repository.findByUserId(userId).stream()
                .map(x -> x.getTopicId())
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "site_deleteSubscribeTopic", groupId = "group-id")
    public SubscribeTopic delete(String subscribeTopic) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SubscribeTopic value = objectMapper.readValue(subscribeTopic, SubscribeTopic.class);
        SubscribeTopic rsl = repository
                .findByUserIdAndTopicId(value.getUserId(), value.getTopicId());
        repository.delete(rsl);
        return value;
    }
}