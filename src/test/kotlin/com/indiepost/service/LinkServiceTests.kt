package com.indiepost.service

import com.indiepost.IndiepostBackendApplication
import com.indiepost.dto.LinkMetadataRequest
import com.indiepost.helper.printToJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import javax.inject.Inject

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [IndiepostBackendApplication::class])
@WebAppConfiguration
class LinkServiceTests {
    @Inject
    private lateinit var linkService: LinkService

    @Test
    fun getLinkBox_shouldWorkProperly() {
        val url = "https://movie.naver.com/movie/bi/mi/basic.nhn?code=171725"
        val linkBoxResponse = linkService.getLinkMetadata(LinkMetadataRequest(url, url))
        assertThat(linkBoxResponse).isNotNull()
        printToJson(linkBoxResponse)
    }

    @Test
    fun searchMovies_shouldWorkProperly() {
        val text = "스타워즈"
        val list = linkService.searchMovies(text, 10)
        assertThat(list).isNotEmpty()
    }
}