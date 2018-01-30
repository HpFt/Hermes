package ru.tykvin.hermes.file.dao;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.tykvin.hermes.file.configuration.StorageConfiguration;
import ru.tykvin.hermes.file.storage.FileInfo;
import ru.tykvin.hermes.tables.records.FilesRecord;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FilesMapper {

    private final StorageConfiguration sc;

    public FileInfo mapToFileInfo(FilesRecord record) {
        Path path = Paths.get(record.getPath());
        return new FileInfo(
                UUID.fromString(record.getId()),
                record.getSize(),
                record.getCreateAt(),
                record.getHash(),
                createUrl(path),
            path
        );
    }

    public String createUrl(Path path) {
        return sc.getHost() + "/download/" + path.getFileName();
    }
}
