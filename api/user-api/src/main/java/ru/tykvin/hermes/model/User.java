package ru.tykvin.hermes.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class User {
    @NonNull
    private final UUID id;
    @NonNull
    private final LocalDateTime createAt;
    @NonNull
    private final String ip;
}
