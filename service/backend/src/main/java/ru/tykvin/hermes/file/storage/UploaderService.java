package ru.tykvin.hermes.file.storage;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.file.configuration.StorageConfiguration;
import ru.tykvin.hermes.file.dao.FilesDao;
import ru.tykvin.hermes.file.model.FileInfo;
import ru.tykvin.hermes.file.model.FilesMapper;
import ru.tykvin.hermes.file.model.FilesystemStorageFile;
import ru.tykvin.hermes.file.model.UploadingEntity;
import ru.tykvin.hermes.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UploaderService {

    private final StorageConfiguration sc;
    private final FilesMapper mapper;
    private final FilesDao filesDao;
    private final FilesystemStorage storage;

    @SneakyThrows
    public List<FileInfo> upload(User user, HttpServletRequest request) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("It's not multipart");
        }
        ServletFileUpload fileUpload = new ServletFileUpload();
        FileItemIterator iterator = fileUpload.getItemIterator(request);
        List<UploadingEntity> uploaded = new ArrayList<>();
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();
            if (item.isFormField()) {
                continue;
            }
            UploadingEntity entity = mapper.mapToUploadingEntity(item);
            if (entity.getSha256() != null) {
                if (filesDao.bindExistsToUser(entity, user)) {
                    continue;
                } else {
                    // Тут логика по докачке файлов
                }
            }
            upload(entity);
            if (filesDao.bindExistsToUser(entity, user)) {
                storage.delete(entity);
                continue;
            }
            filesDao.createFile(entity, user);
            uploaded.add(entity);
        }
        return uploaded.stream().map(entity -> filesDao.findFileInfo(entity.getId()).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void upload(UploadingEntity entity) {
        FilesystemStorageFile file = storage.write(entity);
        entity.setSize(file.getSize());
        entity.setSha256(file.getHash());
        entity.setUploaded(true);
    }

}
