package ru.tykvin.hermes.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TokenData {
    private final User user;
    private final LocalDateTime createAt = LocalDateTime.now();
}
