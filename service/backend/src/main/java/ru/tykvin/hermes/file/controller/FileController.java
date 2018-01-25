package ru.tykvin.hermes.file.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tykvin.hermes.file.dao.StorageDao;
import ru.tykvin.hermes.model.User;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final StorageDao storageDao;

    @PostMapping("/save")
    @SneakyThrows
    public void save(HttpServletRequest request, SecurityContext sc) {
        Object user = sc.getAuthentication().getPrincipal();
        storageDao.save((User) user, request.getInputStream());
    }

}
