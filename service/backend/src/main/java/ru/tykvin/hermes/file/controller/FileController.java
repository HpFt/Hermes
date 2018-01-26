package ru.tykvin.hermes.file.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.tykvin.hermes.file.dao.StorageDao;
import ru.tykvin.hermes.security.CurrentUserHolder;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final StorageDao storageDao;
    private final CurrentUserHolder userHolder;

    @SneakyThrows
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void save(HttpServletRequest request) {
        storageDao.save(userHolder.get(), request);
    }

}
