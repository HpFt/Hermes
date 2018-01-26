package ru.tykvin.hermes.file.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tykvin.hermes.file.storage.Storage;
import ru.tykvin.hermes.security.CurrentUserHolder;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final Storage storage;
    private final CurrentUserHolder userHolder;

    @SneakyThrows
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void save(HttpServletRequest request) {
        storage.save(userHolder.get(), request);
    }

}
