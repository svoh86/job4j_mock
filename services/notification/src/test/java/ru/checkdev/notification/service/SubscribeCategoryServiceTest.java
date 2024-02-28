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
import ru.checkdev.notification.domain.SubscribeCategory;
import ru.checkdev.notification.telegram.TgRun;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import ru.checkdev.notification.web.TemplateController;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest(classes = NtfSrv.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class SubscribeCategoryServiceTest {
    @Autowired
    private SubscribeCategoryService service;

    @MockBean
    private TgRun tgRun;

    @MockBean
    private TgAuthCallWebClint tgAuthCallWebClint;

    @MockBean
    private TemplateController templateController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void whenGetAllSubCatReturnContainsValue() throws JsonProcessingException {
        SubscribeCategory subscribeCategory = this.service.save(
                objectMapper.writeValueAsString(new SubscribeCategory(0, 1, 1)));
        List<SubscribeCategory> result = this.service.findAll();
        assertTrue(result.contains(subscribeCategory));
    }

    @Test
    public void requestByUserIdReturnCorrectValue() throws JsonProcessingException {
        SubscribeCategory subscribeCategory = this.service.save(
                objectMapper.writeValueAsString(new SubscribeCategory(1, 2, 2)));
        List<Integer> result = this.service.findCategoriesByUserId(subscribeCategory.getUserId());
        assertEquals(result, List.of(2));
    }

    @Test
    public void whenDeleteSubCatItIsNotExist() throws JsonProcessingException {
        SubscribeCategory subscribeCategory = this.service.save(
                objectMapper.writeValueAsString(new SubscribeCategory(2, 3, 3)));
        String s = objectMapper.writeValueAsString(subscribeCategory);
        subscribeCategory = this.service.delete(s);
        List<SubscribeCategory> result = this.service.findAll();
        assertFalse(result.contains(subscribeCategory));
    }
}