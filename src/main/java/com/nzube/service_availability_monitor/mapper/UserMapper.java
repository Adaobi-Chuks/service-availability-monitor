package com.nzube.service_availability_monitor.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nzube.service_availability_monitor.dto.user.UserResponse;
import com.nzube.service_availability_monitor.entity.User;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPlatformRole());
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .toList();
    }
}
