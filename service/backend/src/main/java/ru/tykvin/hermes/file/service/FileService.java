package ru.tykvin.hermes.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.file.dao.FilesDao;
import ru.tykvin.hermes.file.model.DownloadingEntity;
import ru.tykvin.hermes.model.User;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FilesDao filesDao;

    public void createFileIfNotExists(DownloadingEntity file, User user) {

    }

}
