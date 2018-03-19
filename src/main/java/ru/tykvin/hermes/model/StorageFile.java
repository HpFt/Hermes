package ru.tykvin.hermes.model;

import lombok.Data;

import java.nio.file.Path;

@Data
public class StorageFile {
    private final Path path;
    private final long size;
    private final String hash;
}
