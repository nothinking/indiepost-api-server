package com.indiepost.config;

import com.indiepost.filter.CORSFilter;
import com.indiepost.interceptor.StatLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Created by jake on 7/31/16.
 */
@Configuration
@EnableWebMvc
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    private final StatLoggingInterceptor statLoggingInterceptor;

    @Autowired
    public WebMvcConfigurer(StatLoggingInterceptor statLoggingInterceptor) {
        this.statLoggingInterceptor = statLoggingInterceptor;
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//
//        registry.addInterceptor(statLoggingInterceptor)
//                .addPathPatterns("/")
//                .addPathPatterns("/post/**")
//                .addPathPatterns("/page/**")
//                .addPathPatterns("/category/**")
//                .addPathPatterns("/search/**")
//                .addPathPatterns("/api/**")
//                .addPathPatterns("/error")
//                .excludePathPatterns("/admin/**")
//                .excludePathPatterns("/api/admin/**");
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/naver08e868adc7d6dcdd0ee15d4f4692dbbc.html")
                .addResourceLocations("file:/data/resources/naver08e868adc7d6dcdd0ee15d4f4692dbbc.html")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
        registry
                .addResourceHandler("/sitemap.xml")
                .addResourceLocations("file:/data/resources/sitemap.xml")
                .setCachePeriod(1800)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
        registry
                .addResourceHandler("/google9e42b214f3b4a31f.html")
                .addResourceLocations("file:/data/resources/google9e42b214f3b4a31f.html")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
        registry
                .addResourceHandler("/robots.txt")
                .addResourceLocations("file:/data/resources/robots.txt")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("file:/data/resources/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations("file:/data/uploads/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
        registry
                .addResourceHandler("/uploadData/**")
                .addResourceLocations("file:/data/uploadData/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CORSFilter());
        registration.addUrlPatterns("/api/**");
        registration.setOrder(1);
        return registration;
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public ViewResolver viewResolver() {
////        ScriptTemplateViewResolver scriptTemplateViewResolver = new ScriptTemplateViewResolver("/public/", ".html");
////        scriptTemplateViewResolver.setOrder(1);
////        return scriptTemplateViewResolver;
//        V8ScriptTemplateViewResolver v8ScriptTemplateViewResolver = new V8ScriptTemplateViewResolver("/public/", ".html");
//        v8ScriptTemplateViewResolver.setOrder(1);
//        return v8ScriptTemplateViewResolver;
//    }
//
//    @Bean
//    public V8ScriptTemplateConfigurer v8ScriptTemplateConfigurer() {
//        return new V8ScriptTemplateConfigurer( "static/polyfill.js", "file:/data/resources/indiepost-react-webapp/dist/server.bundle.js");
//    }
//
////    public ScriptTemplateConfigurer scriptTemplateConfigurer() {
////        ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
////        configurer.setEngineName("nashorn");
////        configurer.setScripts(
////                "static/polyfill.js",
////                "file:/data/resources/indiepost-react-webapp/src/server.js"
////        );
////        configurer.setRenderFunction("render");
////        configurer.setSharedEngine(false);
////        return configurer;
////    }
}
