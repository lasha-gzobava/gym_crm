package org.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "report-service",              // matches your Eureka name or use URL
        url = "${report.service.url:http://localhost:8083}", // fallback local dev URL
        path = "/reports"
)
public interface ReportClient {

    @DeleteMapping("/trainees/{username}")
    void deleteReportsByTrainee(@PathVariable("username") String username);
}
