package ru.tykvin.hermes.file.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class DownloadingEntity {
    private final UUID id;
    private final long size;
    private final LocalDateTime createAt;
    private final String url;
    private final String fileName;
}
