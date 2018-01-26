package ru.tykvin.hermes.file.storage;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class FileInfo {
    private final UUID id;
    private final long size;
    private final LocalDateTime createAt;
    private final String sha256;
    private final Path path;
}
