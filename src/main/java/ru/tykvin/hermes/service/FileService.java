package ru.tykvin.hermes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.dao.FilesDao;
import ru.tykvin.hermes.model.FileInfo;
import ru.tykvin.hermes.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FilesDao filesDao;

    public List<FileInfo> geAllUserFiles(User user) {
        return filesDao.findFilesByUser(user);
    }
}
