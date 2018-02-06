package ru.tykvin.hermes.file.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tykvin.hermes.file.model.FileInfo;
import ru.tykvin.hermes.file.storage.UploaderService;
import ru.tykvin.hermes.security.CurrentUserHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final UploaderService uploader;
    private final CurrentUserHolder userHolder;

    @SneakyThrows
    @PostMapping(value = "/api/file/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<FileInfo> save(HttpServletRequest request) {
        return uploader.upload(userHolder.get(), request);
    }

    @GetMapping("/download/{bindId}")
    @SneakyThrows
    public ResponseEntity<Resource> download(@PathVariable String bindId) {
//        File file = storage.read(UUID.fromString(bindId));
//        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
//
//        return ResponseEntity.ok()
//            .contentLength(file.length())
//            .contentType(MediaType.parseMediaType("application/octet-stream"))
//            .body(resource);
        return null;
    }
}
