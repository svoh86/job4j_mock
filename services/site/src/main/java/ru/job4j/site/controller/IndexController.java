package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.ProfileDTO;
import ru.job4j.site.dto.TopicIdNameDTO;
import ru.job4j.site.service.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static ru.job4j.site.controller.RequestResponseTools.getToken;

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {
    private final CategoriesService categoriesService;
    private final InterviewsService interviewsService;
    private final AuthService authService;
    private final NotificationService notifications;
    private final ProfilesService profilesService;
    private final TopicsService topicsService;

    @GetMapping({"/", "index"})
    public String getIndexPage(Model model, HttpServletRequest req) throws JsonProcessingException {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/"
        );
        Map<CategoryDTO, Long> categories = new ConcurrentHashMap<>();
        List<CategoryDTO> categoryDTOList = categoriesService.getMostPopular();
        for (CategoryDTO categoryDTO : categoryDTOList) {
            long count = 0;
            List<TopicIdNameDTO> topicIdNameDtos = topicsService.getTopicIdNameDtoByCategory(categoryDTO.getId());
            for (TopicIdNameDTO topicIdNameDTO : topicIdNameDtos) {
                Page<InterviewDTO> interviewDTOPage = interviewsService.getByTopicId(topicIdNameDTO.getId(), 0, Integer.MAX_VALUE);
                count += interviewDTOPage.getTotalElements();
                categories.put(categoryDTO, count);
            }
        }
        try {
            model.addAttribute("categories", categories);
            var token = getToken(req);
            if (token != null) {
                var userInfo = authService.userInfo(token);
                model.addAttribute("userInfo", userInfo);
                model.addAttribute("userDTO", notifications.findCategoriesByUserId(userInfo.getId()));
                RequestResponseTools.addAttrCanManage(model, userInfo);
            }
        } catch (Exception e) {
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
        }
        List<InterviewDTO> interviewDTOList = interviewsService.getByType(1);
        Set<ProfileDTO> profileDTOSet = ConcurrentHashMap.newKeySet();
        for (InterviewDTO interviewDTO : interviewDTOList) {
            Optional<ProfileDTO> profileById = profilesService.getProfileById(interviewDTO.getSubmitterId());
            profileById.ifPresent(profileDTOSet::add);
        }
        model.addAttribute("new_interviews", interviewDTOList);
        model.addAttribute("profiles", profileDTOSet);
        return "index";
    }
}