package ru.checkdev.mock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.mock.MockSrv;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.dto.TopicIdNameDTO;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MockSrv.class)
@RunWith(SpringRunner.class)
class CategoriesServiceTest {
    @MockBean
    private TopicsService topicsService;
    @MockBean
    private InterviewService interviewService;
    @Autowired
    private CategoriesService categoriesService;

    @Test
    void whenGetInterviewCount() throws JsonProcessingException {
        when(topicsService.getByCategory(anyInt())).thenReturn(List.of(new TopicIdNameDTO(1, "name")));
        when(interviewService.findByTopicsIds(anyList())).thenReturn(List.of(new Interview()));
        var actual = categoriesService.getInterviewCount(List.of(1, 2));
        assertThat(actual).isEqualTo(Map.of(1, 1L, 2, 1L));
    }
}