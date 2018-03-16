package ru.tykvin.hermes.file.model;

import lombok.Data;
import org.hibernate.validator.internal.util.logging.formatter.DurationFormatter;

import java.time.Duration;
import java.util.UUID;

@Data
public class FileInfo {
    private final UUID bindingId;
    private final UUID fileId;
    private final String fileName;
    private final Duration lifeTime;
    private final Integer downloadsLeft;
    private final long size;
    private final String url;


    public String getLifeTimeAString() {
        return new DurationFormatter(lifeTime).toString();
    }
}
