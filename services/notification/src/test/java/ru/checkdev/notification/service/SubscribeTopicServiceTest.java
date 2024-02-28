package ru.checkdev.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.notification.NtfSrv;
import ru.checkdev.notification.domain.SubscribeTopic;
import ru.checkdev.notification.telegram.TgRun;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import ru.checkdev.notification.web.TemplateController;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest(classes = NtfSrv.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class SubscribeTopicServiceTest {
    @Autowired
    private SubscribeTopicService service;

    @MockBean
    private TgRun tgRun;

    @MockBean
    private TgAuthCallWebClint tgAuthCallWebClint;

    @MockBean
    private TemplateController templateController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void whenGetAllSubTopicReturnContainsValue() throws JsonProcessingException {
        SubscribeTopic subscribeTopic = this.service.save(
                objectMapper.writeValueAsString(new SubscribeTopic(0, 1, 1)));
        List<SubscribeTopic> result = this.service.findAll();
        assertTrue(result.contains(subscribeTopic));
    }

    @Test
    public void requestByUserIdReturnCorrectValue() throws JsonProcessingException {
        SubscribeTopic subscribeTopic = this.service.save(
                objectMapper.writeValueAsString(new SubscribeTopic(1, 2, 2)));
        List<Integer> result = this.service.findTopicByUserId(subscribeTopic.getUserId());
        assertEquals(result, List.of(2));
    }

    @Test
    public void whenDeleteTopicCatItIsNotExist() throws JsonProcessingException {
        SubscribeTopic subscribeTopic = this.service.save(
                objectMapper.writeValueAsString(new SubscribeTopic(2, 3, 3)));
        subscribeTopic = this.service.delete(String.valueOf(subscribeTopic));
        List<SubscribeTopic> result = this.service.findAll();
        assertFalse(result.contains(subscribeTopic));
    }
}