package ru.tykvin.hermes.file.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.tykvin.hermes.file.model.DownloadingEntity;
import ru.tykvin.hermes.file.storage.Storage;
import ru.tykvin.hermes.security.CurrentUserHolder;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final Storage storage;
    private final CurrentUserHolder userHolder;

    @SneakyThrows
    @PostMapping(value = "/api/file/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<DownloadingEntity> save(HttpServletRequest request) {
        return storage.save(userHolder.get(), request);
    }

    @GetMapping("/download/{bindId}")
    @SneakyThrows
    public ResponseEntity<Resource> download(@PathVariable String bindId) {
        File file = storage.read(UUID.fromString(bindId));
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
            .contentLength(file.length())
            .contentType(MediaType.parseMediaType("application/octet-stream"))
            .body(resource);
    }
}
