package com.example.boardpjt.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

// JPA -> Spring Data Redis
@Getter
@NoArgsConstructor
@AllArgsConstructor // Record로 대체가 가능하긴 함
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 7)
// Refresh Token의 만료와 일치하면 베스트인데 꼭 일치할 것까지는...
public class RefreshToken {
    @Id
    private String username;
    private String token;
}
