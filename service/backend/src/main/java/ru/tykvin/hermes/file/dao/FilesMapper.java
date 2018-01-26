package ru.tykvin.hermes.file.dao;

import org.springframework.stereotype.Component;
import ru.tykvin.hermes.file.storage.FileInfo;
import ru.tykvin.hermes.tables.records.FilesRecord;

import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FilesMapper {
    public FileInfo mapToFileInfo(FilesRecord record) {
        return new FileInfo(
                UUID.fromString(record.getId()),
                record.getSize(),
                record.getCreateAt(),
                record.getHash(),
                Paths.get(record.getPath())
        );
    }
}
