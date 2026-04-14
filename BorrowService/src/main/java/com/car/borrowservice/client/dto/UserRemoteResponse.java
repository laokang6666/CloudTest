package com.car.borrowservice.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 与 UserService 返回 JSON 字段对齐，供 Feign 反序列化。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRemoteResponse {

    private Long id;
    private String loginName;
    private String displayName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
