package org.example.config;

import org.example.util.TxIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"org.example", "org.springdoc"})
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
        registry
                .addResourceHandler("/v3/api-docs/**", "/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    @Bean
    public FilterRegistrationBean<TxIdFilter> txIdFilter() {
        FilterRegistrationBean<TxIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new TxIdFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(1); // run early
        return reg;
    }
}
