package ru.tykvin.hermes.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TokenData {
    private final User user;
    private final LocalDateTime createAt;
}
