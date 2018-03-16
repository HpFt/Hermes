package ru.tykvin.hermes.file.service;

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
import ru.tykvin.hermes.file.model.FilesystemStorageFile;
import ru.tykvin.hermes.file.model.UploadingEntity;
import ru.tykvin.hermes.file.storage.FilesystemStorage;
import ru.tykvin.hermes.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final StorageConfiguration sc;
    private final FilesMapper mapper;
    private final FilesDao filesDao;
    private final FilesystemStorage storage;

    @SneakyThrows
    public Set<DownloadingEntity> upload(User user, HttpServletRequest request) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("It's not multipart");
        }
        ServletFileUpload fileUpload = new ServletFileUpload();
        FileItemIterator iterator = fileUpload.getItemIterator(request);
        Set<DownloadingEntity> result = new HashSet<>();
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();
            if (item.isFormField()) {
                continue;
            }
            UploadingEntity entity = mapper.mapToUploadingEntity(item);
            Optional<DownloadingEntity> bound;
            if (entity.getSha256() != null) {
                bound = filesDao.bindExistsToUser(entity, user);
            } else {
                upload(entity);
                if (entity.getSize() == 0) {
                    storage.delete(entity);
                    continue;
                }
                bound = filesDao.bindExistsToUser(entity, user);
                if (bound.isPresent()) {
                    storage.delete(entity);
                } else {
                    bound = filesDao.createFile(entity, user);
                }
            }
            bound.ifPresent(result::add);
        }
        return result;
    }

    private void upload(UploadingEntity entity) {
        FilesystemStorageFile file = storage.write(entity);
        entity.setSize(file.getSize());
        entity.setSha256(file.getHash());
        entity.setUploaded(true);
    }

}
