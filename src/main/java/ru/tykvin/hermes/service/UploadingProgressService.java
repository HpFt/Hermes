package ru.tykvin.hermes.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.model.UploadInitItem;
import ru.tykvin.hermes.model.UploadingProgress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UploadingProgressService {

    private final Cache<UUID, UploadingProgress> progressMap = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    public UploadingProgress createProgress(List<UploadInitItem> uploadInitItems) {
        UUID id = UUID.randomUUID();
        UploadingProgress progress = UploadingProgress.builder()
                .id(id)
                .total(uploadInitItems.size())
                .bytesTotal(uploadInitItems.stream().mapToLong(UploadInitItem::getSize).sum())
                .build();
        progressMap.put(id, progress);
        return progress;
    }

    public void deleteProgress(UUID id) {
        progressMap.invalidate(id);
    }

    public UploadingProgress getProgress(UUID id) {
        return Optional.ofNullable(progressMap.getIfPresent(id)).orElseThrow(IllegalArgumentException::new);
    }

}
