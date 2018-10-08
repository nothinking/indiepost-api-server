package com.indiepost.service;

import com.indiepost.NewIndiepostApplicationKt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.net.MalformedURLException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Created by jake on 17. 3. 21.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = NewIndiepostApplicationKt.class)
@WebAppConfiguration
public class SitemapServiceTests {

    @Inject
    private SitemapService sitemapService;

    @Test
    public void sitemapShouldCreateCorrectly() throws MalformedURLException {
        String sitemap = sitemapService.buildSitemap();
        assertThat(sitemap).isNotEmpty();
        System.out.println(sitemap);
    }
}
