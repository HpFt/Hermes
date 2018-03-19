package ru.tykvin.hermes.dao;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.configuration.StorageConfiguration;
import ru.tykvin.hermes.model.DownloadingEntity;
import ru.tykvin.hermes.model.FileInfo;
import ru.tykvin.hermes.model.UploadingEntity;
import ru.tykvin.hermes.tables.records.VFileInfoRecord;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilesMapper {
    private final StorageConfiguration sc;


    public DownloadingEntity mapToDownloadingEntity(VFileInfoRecord record) {
        return new DownloadingEntity(
                UUID.fromString(record.getBindid()),
                record.getSize(),
                record.getExpiration(),
                record.getName()
        );
    }

    public String createUrl(String id) {
        return sc.getHost() + "/download/" + id;
    }

    public FileInfo mapToFileInfo(VFileInfoRecord record) {
        return new FileInfo(
                UUID.fromString(record.getBindid()),
                UUID.fromString(record.getFileid()),
                record.getName(),
                Duration.between(LocalDateTime.now(), record.getExpiration()),
                record.getDownloadsleft(),
                record.getSize(),
                createUrl(record.getBindid())
        );
    }

    @SneakyThrows
    public UploadingEntity mapToUploadingEntity(FileItemStream item) {
        FileItemHeaders headers = item.getHeaders();
        return UploadingEntity.builder()
                .item(item)
                .fileName(item.getName())
                .sha256(headers.getHeader("sha256"))
                .build();
    }
}
