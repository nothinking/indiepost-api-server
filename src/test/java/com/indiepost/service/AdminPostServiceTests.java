package com.indiepost.service;

import com.indiepost.NewIndiepostApplication;
import com.indiepost.dto.post.AdminPostRequestDto;
import com.indiepost.dto.post.AdminPostResponseDto;
import com.indiepost.dto.post.AdminPostSummaryDto;
import com.indiepost.enums.Types;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NewIndiepostApplication.class)
@WebAppConfiguration
public class AdminPostServiceTests {

    @Inject
    private AdminPostService service;

    private List<Long> insertedIds = new ArrayList<>();

    @Before
    public void insert() {
        AdminPostRequestDto post = new AdminPostRequestDto();
        post.setTags(Arrays.asList("여행기", "콩자반", "일본영화", "쿠바", "아기다리고기다", "로망포르노", "로맨스"));
        post.setContributors(Arrays.asList("유미래", "최은제", "이사민", "김유영"));
        post.setContent("test content");
        post.setTitle("test title");
        post.setExcerpt("test except");
        post.setCategoryId(2L);
        post.setDisplayName("TEST name");
        post.setPublishedAt(Instant.now());
        insertedIds.add(service.createDraft(post));
    }

    @After
    public void deleteInserted() {
        for (Long id : insertedIds) {
            service.deleteById(id);
        }
    }

    @Test
    @WithMockUser(username = "eunjechoi")
    public void saveAutosave_shouldReturnCreatedPostId() {
        AdminPostRequestDto post = new AdminPostRequestDto();
        post.setContent("test content");
        post.setTitle("test title");
        post.setExcerpt("test except");
        post.setCategoryId(2L);
        post.setDisplayName("TEST name");
        post.setPublishedAt(Instant.now());
        Long id = service.createAutosave(post);
        if (id != null) {
            insertedIds.add(id);
        }
        assertThat(id).isNotNull();
    }

    @Test
    @WithMockUser(username = "eunjechoi")
    public void retrievedAdminPostResponseDto_shouldContainTagsWithProperOrder() {
        for (Long id : insertedIds) {
            AdminPostResponseDto dto = service.findOne(id);
            assertThat(dto.getTags()).isEqualTo(Arrays.asList("여행기", "콩자반", "일본영화", "쿠바", "아기다리고기다", "로망포르노", "로맨스"));
        }
    }

    @Test
    @WithMockUser(username = "eunjechoi")
    public void retrievedAdminPostResponseDto_shouldContainContributorsWithProperOrder() {
        for (Long id : insertedIds) {
            AdminPostResponseDto dto = service.findOne(id);
            assertThat(dto.getContributors()).isEqualTo(Arrays.asList("유미래", "최은제", "이사민", "김유영"));
        }
    }

    @Test
    @WithMockUser(username = "indiepost")
    public void findText_shouldReturnResultProperly() {
        Page<AdminPostSummaryDto> page =
                service.findText("인스타그램", Types.PostStatus.PUBLISH, PageRequest.of(0, 500));
        assertThat(page.getContent()).hasSize(18);

    }
}
