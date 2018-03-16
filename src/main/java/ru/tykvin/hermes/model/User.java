package ru.tykvin.hermes.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class User {
    @NonNull
    private final UUID id;
    @NonNull
    private final OffsetDateTime createAt;
    @NonNull
    private final String ip;
}
