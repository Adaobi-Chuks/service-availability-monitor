package com.nzube.service_availability_monitor.checker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.nzube.service_availability_monitor.entity.MonitoredService;
import com.nzube.service_availability_monitor.entity.ServiceCheck;
import com.nzube.service_availability_monitor.enums.ServiceStatus;
import com.nzube.service_availability_monitor.repository.ServiceCheckRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpChecker {

    private static final int TIMEOUT_SECONDS = 10;

    private final ServiceCheckRepository serviceCheckRepository;

    public ServiceCheck check(MonitoredService service) {
        long startTime = System.currentTimeMillis();
        ServiceStatus status;
        Integer statusCode = null;
        String errorMessage = null;

        try {
            HttpURLConnection connection = createConnection(service.getUrl());
            statusCode = connection.getResponseCode();
            status = isSuccessful(statusCode) ? ServiceStatus.UP : ServiceStatus.DOWN;
            connection.disconnect();
        } catch (SocketTimeoutException e) {
            status = ServiceStatus.DOWN;
            errorMessage = "Connection timed out after " + TIMEOUT_SECONDS + " seconds";
        } catch (UnknownHostException e) {
            status = ServiceStatus.DOWN;
            errorMessage = "Unknown host: " + service.getUrl();
        } catch (IOException e) {
            status = ServiceStatus.DOWN;
            errorMessage = "Connection failed: " + e.getMessage();
        }

        long responseTimeMs = System.currentTimeMillis() - startTime;

        ServiceCheck result = ServiceCheck.builder()
                .service(service)
                .status(status)
                .statusCode(statusCode)
                .responseTimeMs(responseTimeMs)
                .errorMessage(errorMessage)
                .checkedAt(LocalDateTime.now())
                .build();

        serviceCheckRepository.save(result);
        log.info("HTTP check for {} → {} ({}ms)", service.getUrl(), status, responseTimeMs);
        return result;
    }

    private HttpURLConnection createConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(TIMEOUT_SECONDS * 1000);
        connection.setReadTimeout(TIMEOUT_SECONDS * 1000);
        connection.setInstanceFollowRedirects(true);
        return connection;
    }

    private boolean isSuccessful(int statusCode) {
        return statusCode >= 200 && statusCode < 400;
    }
}
