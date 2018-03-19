package ru.tykvin.hermes.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tykvin.hermes.model.DownloadingEntity;
import ru.tykvin.hermes.model.UploadInitItem;
import ru.tykvin.hermes.model.UploadingProgress;
import ru.tykvin.hermes.security.CurrentUserHolder;
import ru.tykvin.hermes.service.UploadService;
import ru.tykvin.hermes.service.UploadingProgressService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/upload")
public class UploadingController {

    private final UploadingProgressService uploadingProgressService;
    private final UploadService uploadService;
    private final CurrentUserHolder userHolder;

    @PostMapping("/init")
    public UploadingProgress init(@NonNull @RequestBody List<UploadInitItem> items) {
        return uploadingProgressService.createProgress(items);
    }

    @GetMapping("/progress")
    public UploadingProgress getProgress(@NonNull @RequestParam("id") UUID progressId) {
        return uploadingProgressService.getProgress(progressId);
    }


    @SneakyThrows
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Set<DownloadingEntity> save(HttpServletRequest request, @RequestHeader("X-UploadId") UUID uploadId) {
        return uploadService.upload(userHolder.get(), uploadId, request);
    }

}
