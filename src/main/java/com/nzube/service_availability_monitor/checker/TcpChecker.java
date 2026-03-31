package com.nzube.service_availability_monitor.checker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
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
public class TcpChecker {

    private static final int TIMEOUT_SECONDS = 10;

    private final ServiceCheckRepository serviceCheckRepository;

    public ServiceCheck check(MonitoredService service) {
        long startTime = System.currentTimeMillis();
        ServiceStatus status;
        String errorMessage = null;

        try (Socket socket = new Socket()) {
            socket.connect(
                    new InetSocketAddress(service.getUrl(), service.getPort()),
                    TIMEOUT_SECONDS * 1000);
            status = ServiceStatus.UP;
        } catch (SocketTimeoutException e) {
            status = ServiceStatus.DOWN;
            errorMessage = "Connection timed out after " + TIMEOUT_SECONDS + " seconds";
        } catch (IOException e) {
            status = ServiceStatus.DOWN;
            errorMessage = "Connection failed: " + e.getMessage();
        }

        long responseTimeMs = System.currentTimeMillis() - startTime;

        ServiceCheck result = ServiceCheck.builder()
                .service(service)
                .status(status)
                .statusCode(null) // TCP has no status code
                .responseTimeMs(responseTimeMs)
                .errorMessage(errorMessage)
                .checkedAt(LocalDateTime.now())
                .build();

        serviceCheckRepository.save(result);
        log.info("TCP check for {}:{} → {} ({}ms)", service.getUrl(), service.getPort(), status, responseTimeMs);
        return result;
    }
}
