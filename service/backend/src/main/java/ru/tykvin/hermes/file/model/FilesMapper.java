package ru.tykvin.hermes.file.model;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.springframework.stereotype.Component;
import ru.tykvin.hermes.file.configuration.StorageConfiguration;
import ru.tykvin.hermes.tables.records.FilesRecord;
import ru.tykvin.hermes.tables.records.VFileInfoRecord;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FilesMapper {
    private final StorageConfiguration sc;


    public DownloadingEntity mapToDownloadingEntity(FilesRecord record) {
        Path path = Paths.get(record.getPath());
        return new DownloadingEntity(
                UUID.fromString(record.getId()),
                record.getSize(),
                record.getCreateAt(),
                record.getHash(),
                createUrl(path),
                "",
                path
        );
    }

    public String createUrl(String id) {
        return sc.getHost() + "/download/" + id;
    }

    public FileInfo mapToFileInfo(VFileInfoRecord record) {
        return new FileInfo(
                UUID.fromString(record.getId()),
                record.getName(),
                Duration.between(LocalDateTime.now(), record.getExpiration()),
                record.getSize(),
                createUrl(record.getId())
        );
    }

    public UploadingEntity mapToUploadingEntity(FileItemStream item) {
        FileItemHeaders headers = item.getHeaders();
        return UploadingEntity.builder()
                .item(item)
                .fileName(item.getFieldName())
                .sha256(headers.getHeader("sha256"))
                .build();
    }
}
