package ru.tykvin.hermes.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.file.dao.FilesDao;
import ru.tykvin.hermes.file.model.FileInfo;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DownloadService {

    private final FilesDao filesDao;

    public Optional<FileInfo> read(UUID bindId) {
        return filesDao.findFileInfo(bindId);
    }

}
