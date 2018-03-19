package ru.tykvin.hermes.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.configuration.StorageConfiguration;
import ru.tykvin.hermes.dao.FilesDao;
import ru.tykvin.hermes.dao.FilesMapper;
import ru.tykvin.hermes.dao.FilesystemStorage;
import ru.tykvin.hermes.model.DownloadingEntity;
import ru.tykvin.hermes.model.StorageFile;
import ru.tykvin.hermes.model.UploadingEntity;
import ru.tykvin.hermes.model.UploadingProgress;
import ru.tykvin.hermes.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final StorageConfiguration sc;
    private final FilesMapper mapper;
    private final FilesDao filesDao;
    private final FilesystemStorage storage;
    private final UploadingProgressService uploadingProgressService;

    @SneakyThrows
    public Set<DownloadingEntity> upload(User user, UUID uploadId, HttpServletRequest request) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("It's not multipart");
        }
        UploadingProgress progress = uploadingProgressService.getProgress(uploadId);
        try {
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
                    upload(entity, progress);
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
                progress.plusFile();
            }
            return result;
        } finally {
            uploadingProgressService.deleteProgress(progress.getId());
        }
    }

    private void upload(UploadingEntity entity, UploadingProgress progress) {
        StorageFile file = storage.write(entity, progress);
        entity.setSize(file.getSize());
        entity.setSha256(file.getHash());
        entity.setUploaded(true);
    }

}
