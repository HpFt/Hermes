package ru.tykvin.hermes.file.model;

import lombok.Data;

import java.time.Duration;
import java.util.UUID;

@Data
public class FileInfo {
    private final UUID id;
    private final String fileName;
    private final Duration lifeTime;
    private final long size;
    private final String url;
}
