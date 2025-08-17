package org.example.config;

import org.example.util.TxIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import jakarta.servlet.DispatcherType;
import java.util.EnumSet;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<TxIdFilter> txIdFilter() {
        FilterRegistrationBean<TxIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new TxIdFilter());
        reg.addUrlPatterns("/*");
        // capture REQUEST + ASYNC + FORWARD (and ERROR if you want it on error pages too)
        reg.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.FORWARD));
        // Run early so every log line has the txId (earlier than Spring Security if you use it)
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }
}
