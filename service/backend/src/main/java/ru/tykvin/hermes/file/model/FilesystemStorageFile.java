package ru.tykvin.hermes.file.model;

import lombok.Data;

import java.nio.file.Path;

@Data
public class FilesystemStorageFile {
    private final Path path;
    private final long size;
    private final String hash;
}
