package ru.tykvin.hermes.file.storage;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileBindingConstraints {
    private final LocalDateTime expiration;
    private final long maxDownloads;
}
