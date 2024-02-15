package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.site.SiteSrv;
import ru.job4j.site.domain.Breadcrumb;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.TopicDTO;
import ru.job4j.site.dto.TopicIdNameDTO;
import ru.job4j.site.service.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


/**
 * CheckDev пробное собеседование
 * IndexControllerTest тесты на контроллер IndexController
 *
 * @author Dmitry Stepanov
 * @version 24.09.2023 21:50
 */
@SpringBootTest(classes = SiteSrv.class)
@AutoConfigureMockMvc
class IndexControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoriesService categoriesService;
    @MockBean
    private TopicsService topicsService;
    @MockBean
    private InterviewsService interviewsService;
    @MockBean
    private AuthService authService;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private ProfilesService profilesService;

    private IndexController indexController;

    @BeforeEach
    void initTest() {
        this.indexController = new IndexController(
                categoriesService, interviewsService, authService, notificationService, profilesService, topicsService
        );
    }

    @Test
    void whenGetIndexPageThenReturnIndex() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void whenGetIndexPageExpectModelAttributeThenOk() throws JsonProcessingException {
        var topicDTO1 = new TopicDTO();
        topicDTO1.setId(1);
        topicDTO1.setName("topic1");
        var topicDTO2 = new TopicDTO();
        topicDTO2.setId(2);
        topicDTO2.setName("topic2");
        var cat1 = new CategoryDTO(1, "name1");
        var cat2 = new CategoryDTO(2, "name2");
        var topicIdNameDTO = new TopicIdNameDTO();
        var topicIdNameDTO2 = new TopicIdNameDTO();
        List<CategoryDTO> categories = List.of(cat1, cat2);
        var listCat = List.of(cat1, cat2);
        var firstInterview = new InterviewDTO(1, 1, 1, 1,
                "interview1", "description1", "contact1",
                "30.02.2024", "09.10.2023", 1);
        var secondInterview = new InterviewDTO(2, 1, 1, 2,
                "interview2", "description2", "contact2",
                "30.02.2024", "09.10.2023", 1);
        var listInterviews = List.of(firstInterview, secondInterview);
        Page<InterviewDTO> interviewDTOPage = new PageImpl<>(List.of(firstInterview));

        when(topicsService.getByCategory(cat1.getId())).thenReturn(List.of(topicDTO1));
        when(topicsService.getByCategory(cat2.getId())).thenReturn(List.of(topicDTO2));
        when(categoriesService.getMostPopular()).thenReturn(listCat);
        when(interviewsService.getByType(1)).thenReturn(listInterviews);
        when(topicsService.getTopicIdNameDtoByCategory(cat1.getId())).thenReturn(List.of(topicIdNameDTO, topicIdNameDTO2));
        when(topicsService.getTopicIdNameDtoByCategory(cat2.getId())).thenReturn(List.of(topicIdNameDTO, topicIdNameDTO2));
        when(interviewsService.getByTopicsIds(List.of(0, 0), 0, Integer.MAX_VALUE)).thenReturn(interviewDTOPage);
        when(mock(PageImpl.class).getTotalElements()).thenReturn(1L);

        var listBread = List.of(new Breadcrumb("Главная", "/"));
        var model = new ConcurrentModel();
        var view = indexController.getIndexPage(model, null);
        var actualCategories = model.getAttribute("categories");
        var actualBreadCrumbs = model.getAttribute("breadcrumbs");
        var actualUserInfo = model.getAttribute("userInfo");
        var actualInterviews = model.getAttribute("new_interviews");

        assertThat(view).isEqualTo("index");
        assertThat(actualCategories).usingRecursiveComparison().isEqualTo(categories);
        assertThat(actualBreadCrumbs).usingRecursiveComparison().isEqualTo(listBread);
        assertThat(actualUserInfo).isNull();
        assertThat(actualInterviews).usingRecursiveComparison().isEqualTo(listInterviews);
    }
}