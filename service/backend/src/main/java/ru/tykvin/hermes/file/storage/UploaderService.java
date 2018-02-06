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
import ru.tykvin.hermes.file.model.FileInfo;
import ru.tykvin.hermes.file.model.FilesMapper;
import ru.tykvin.hermes.file.model.FilesystemStorageFile;
import ru.tykvin.hermes.file.model.UploadingEntity;
import ru.tykvin.hermes.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
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
        Map<String, UploadingEntity> hasHash = new HashMap<>();
        List<UploadingEntity> hasntHash = new ArrayList<>();
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();
            if (!item.isFormField()) {
                UploadingEntity preparation = mapper.mapToUploadingEntity(item);
                if (preparation.getSha256() != null) {
                    hasHash.putIfAbsent(preparation.getSha256(), preparation);
                } else {
                    hasntHash.add(preparation);
                }
            }
        }
        // Если клиент не передал hash для файлов - скачиваем и считаем, обновляем значения UpdateEntity
        // Удаляем дубликаты, если они были скачены
        hasntHash.stream().filter(e -> Objects.isNull(e.getSha256())).forEach(entity -> {
            upload(entity);
            if (hasHash.get(entity.getSha256()) == null) {
                hasHash.put(entity.getSha256(), entity);
            } else {
                storage.delete(hasHash.remove(entity.getSha256()));
            }
        });
        Collection<UploadingEntity> prepared = hasHash.values();
        // Пытаемся найти дубликаты в системе и связать их с пользователем
        List<UploadingEntity> existing = prepared
                .stream()
                .filter(entity -> filesDao.bindExistsToUser(entity, user))
                .collect(Collectors.toList());

        // Удаляем скаченные файлы, если удалось
        storage.delete(existing);
        prepared.removeAll(existing);

        // Остались только оригинальные файлы. Докачем те, что еще остались
        prepared.stream().filter(e -> !e.isUploaded()).forEach(this::upload);

        //Сохраним информацию о файлах и свяжем их с пользователем
        prepared.forEach(entity -> filesDao.createFile(entity, user));

        return hasHash.values().stream().map(entity -> filesDao.findFileInfo(entity.getId()).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void upload(UploadingEntity entity) {
        FilesystemStorageFile file = storage.write(entity);
        entity.setSize(file.getSize());
        entity.setSha256(file.getHash());
        entity.setUploaded(true);
    }

}
