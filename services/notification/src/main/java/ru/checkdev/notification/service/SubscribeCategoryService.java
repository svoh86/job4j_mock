package ru.checkdev.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.SubscribeCategory;
import ru.checkdev.notification.repository.SubscribeCategoryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@EnableKafka
public class SubscribeCategoryService {
    private final SubscribeCategoryRepository repository;

    public List<SubscribeCategory> findAll() {
        return repository.findAll();
    }

    @KafkaListener(topics = "site_addSubscribeCategory", groupId = "group-id")
    public SubscribeCategory save(String subscribeCategory) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SubscribeCategory value = objectMapper.readValue(subscribeCategory, SubscribeCategory.class);
        return repository.save(value);
    }

    public List<Integer> findCategoriesByUserId(int userId) {
        List<Integer> rsl = new ArrayList<>();
        List<SubscribeCategory> list = repository.findByUserId(userId);
        for (SubscribeCategory subscribeCategory : list) {
            rsl.add(subscribeCategory.getCategoryId());
        }
        return rsl;
    }

    @KafkaListener(topics = "site_deleteSubscribeCategory", groupId = "group-id")
    public SubscribeCategory delete(String subscribeCategory) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SubscribeCategory value = objectMapper.readValue(subscribeCategory, SubscribeCategory.class);
        SubscribeCategory subscribeCategoryRsl = repository
                .findByUserIdAndCategoryId(value.getUserId(), value.getCategoryId());
        repository.delete(subscribeCategoryRsl);
        return value;
    }
}