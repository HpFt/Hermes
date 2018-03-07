package ru.tykvin.hermes.file.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class DownloadingEntity {
    private final UUID id;
    private final long size;
    private final OffsetDateTime createAt;
    private final String fileName;
}
