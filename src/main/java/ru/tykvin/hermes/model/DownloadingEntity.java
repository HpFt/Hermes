package ru.tykvin.hermes.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(of = "bindId")
public class DownloadingEntity {
    private final UUID bindId;
    private final long size;
    private final OffsetDateTime createAt;
    private final String fileName;
}
