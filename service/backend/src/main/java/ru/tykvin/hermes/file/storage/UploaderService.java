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
import ru.tykvin.hermes.file.model.FilesystemStorageFile;
import ru.tykvin.hermes.file.model.UploadingEntity;
import ru.tykvin.hermes.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UploaderService {

    private final StorageConfiguration sc;
    private final FilesMapper mapper;
    private final FilesDao filesDao;
    private final FilesystemStorage storage;

    @SneakyThrows
    public List<DownloadingEntity> upload(User user, HttpServletRequest request) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("It's not multipart");
        }
        ServletFileUpload fileUpload = new ServletFileUpload();
        FileItemIterator iterator = fileUpload.getItemIterator(request);
        List<DownloadingEntity> result = new ArrayList<>();
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
                bound = filesDao.bindExistsToUser(entity, user);
                if (bound.isPresent()) {
                    storage.delete(entity);
                    continue;
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
