package ru.checkdev.notification.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.checkdev.notification.domain.SubscribeTopic;
import ru.checkdev.notification.service.SubscribeTopicService;

import java.util.List;

@RestController
@RequestMapping("/subscribeTopic")
@AllArgsConstructor
public class SubscribeTopicController {
    private final SubscribeTopicService service;

    @GetMapping("/{id}")
    public ResponseEntity<List<Integer>> findTopicByUserId(@PathVariable int id) {
        List<Integer> list = service.findTopicByUserId(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<SubscribeTopic> toAddSubscribeTopic(
            String subscribetopic) throws JsonProcessingException {
        var created = service.save(subscribetopic);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/delete")
    public ResponseEntity<SubscribeTopic> toDeleteSubscribeTopic(
            String subscribeTopic) throws JsonProcessingException {
        var deleted = service.delete(subscribeTopic);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }
}