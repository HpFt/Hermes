package ru.tykvin.hermes.file.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.http.fileupload.FileItemStream;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
public class UploadingEntity {
    private final FileItemStream item;
    private final UUID id = UUID.randomUUID();
    private final LocalDateTime created = LocalDateTime.now();
    private long size;
    private String fileName;
    private String sha256;
    private boolean tmp;
    private boolean uploaded;
}
