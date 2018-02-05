package ru.tykvin.hermes.file.storage;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FileInfo implements Comparable<FileInfo> {
    private final UUID id;
    private final long size;
    private final LocalDateTime createAt;
    private final String sha256;
    private final String url;
    private final String fileName;
    @JsonIgnore
    private final Path path;

    @Override
    public int compareTo(FileInfo o) {
        return id.compareTo(o.id);
    }
}
