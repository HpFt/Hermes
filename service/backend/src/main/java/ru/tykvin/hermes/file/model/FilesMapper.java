package ru.tykvin.hermes.file.model;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.tykvin.hermes.file.configuration.StorageConfiguration;
import ru.tykvin.hermes.tables.records.VFileInfoRecord;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FilesMapper {
    private final StorageConfiguration sc;


    public DownloadingEntity mapToDownloadingEntity(VFileInfoRecord record) {
        return new DownloadingEntity(
                UUID.fromString(record.getId()),
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
                UUID.fromString(record.getId()),
                record.getName(),
                Duration.between(LocalDateTime.now(), record.getExpiration()),
                record.getSize(),
                createUrl(record.getId())
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
