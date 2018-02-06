package ru.tykvin.hermes.file.storage;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.file.configuration.StorageConfiguration;
import ru.tykvin.hermes.file.dao.FilesDao;
import ru.tykvin.hermes.file.model.DownloadingEntity;
import ru.tykvin.hermes.file.model.FilesMapper;
import ru.tykvin.hermes.file.model.UploadingEntity;
import ru.tykvin.hermes.model.User;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UploaderService {

    private final StorageConfiguration sc;
    private final FilesMapper mapper;
    private final FilesDao filesDao;
    private final FilesystemStorage storage;

    @SneakyThrows
    void upload(User user, HttpServletRequest request) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("It's not multipart");
        }
        ServletFileUpload fileUpload = new ServletFileUpload();
        FileItemIterator iterator = fileUpload.getItemIterator(request);
        List<UploadingEntity> prepared = new ArrayList<>();
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();
            if (!item.isFormField()) {
                UploadingEntity preparation = mapper.mapToUploadingEntity(item);
                prepared.add(preparation);
            }
        }
        prepared.stream().filter(e -> Objects.isNull(e.getSha256())).forEach(storage::uploadTmp);
        prepared.stream().filter(e -> filesDao.bindExistsToUser())
    }

}
