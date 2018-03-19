package ru.tykvin.hermes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.dao.FilesDao;
import ru.tykvin.hermes.model.FileInfo;

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
