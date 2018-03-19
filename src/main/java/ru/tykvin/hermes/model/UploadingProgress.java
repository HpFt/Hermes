package ru.tykvin.hermes.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.convert.DurationFormat;
import org.springframework.boot.convert.DurationStyle;

import java.time.Duration;
import java.util.UUID;

@Builder
@Getter
@Setter
public class UploadingProgress {

    private UUID id;
    private int count;
    private int total;
    private long bytes;
    private long bytesTotal;
    @DurationFormat(DurationStyle.ISO8601)
    private Duration timePassedDuration = Duration.ZERO;
    @DurationFormat(DurationStyle.ISO8601)
    private Duration timeLeftDuration = Duration.ZERO;

    public synchronized void plusFile() {
        count++;
    }

    public synchronized void plusBytes(int bufferSize) {
        bytes += bufferSize;
    }
}
