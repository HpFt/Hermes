package ru.tykvin.hermes.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.file.dao.FilesDao;
import ru.tykvin.hermes.file.model.FileInfo;
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
