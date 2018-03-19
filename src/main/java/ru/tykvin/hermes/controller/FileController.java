package ru.tykvin.hermes.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tykvin.hermes.configuration.StorageConfiguration;
import ru.tykvin.hermes.model.DownloadingEntity;
import ru.tykvin.hermes.model.FileInfo;
import ru.tykvin.hermes.service.DownloadService;
import ru.tykvin.hermes.service.UploadService;
import ru.tykvin.hermes.security.CurrentUserHolder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {
    private final UploadService uploader;
    private final DownloadService downloader;
    private final CurrentUserHolder userHolder;
    private final StorageConfiguration sc;

    @GetMapping("/download/{bindId}")
    @SneakyThrows
    public ResponseEntity<Resource> download(@PathVariable String bindId) {
        Optional<FileInfo> fileOptional = downloader.read(UUID.fromString(bindId));

        if (!fileOptional.isPresent()) {
            return ResponseEntity.noContent().build();
        }

        FileInfo file = fileOptional.get();
        InputStreamResource resource = new InputStreamResource(new FileInputStream(asFile(file)));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(file.getFileName())
                        .size(file.getSize())
                        .build());
        headers.setContentLength(file.getSize());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(resource);
    }

    private File asFile(FileInfo fileInfo) {
        return Paths.get(sc.getRoot()).resolve(fileInfo.getFileId().toString()).toFile();
    }
}
