package ru.tykvin.hermes.file.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class DownloadingEntity implements Comparable<DownloadingEntity> {
    private final UUID id;
    private final long size;
    private final LocalDateTime createAt;
    private final String sha256;
    private final String url;
    private final String fileName;
    private final Path path;

    @Override
    public int compareTo(DownloadingEntity o) {
        if (o == null) {
            return -1;
        }
        return id.compareTo(o.id);
    }
}
