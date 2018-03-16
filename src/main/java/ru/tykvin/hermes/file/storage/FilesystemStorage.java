package ru.tykvin.hermes.file.storage;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.file.configuration.StorageConfiguration;
import ru.tykvin.hermes.file.dao.FilesDao;
import ru.tykvin.hermes.file.model.FilesMapper;
import ru.tykvin.hermes.file.model.FilesystemStorageFile;
import ru.tykvin.hermes.file.model.UploadingEntity;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.io.IOUtils.EOF;

@Service
@RequiredArgsConstructor
public class FilesystemStorage implements Storage {
    private final StorageConfiguration sc;
    private final FilesDao filesDao;
    private final FilesMapper mapper;

    public File read(UUID fileId) {
        return resolveFile(fileId.toString()).toFile();
    }

    @Override
    @SneakyThrows
    public FilesystemStorageFile write(UploadingEntity uploadingEntity) {
        String name = uploadingEntity.getId().toString();
        Path tmpPath = resolveFile(name + ".uploading");
        FileUtils.touch(tmpPath.toFile());
        try (InputStream is = uploadingEntity.getItem().openStream(); OutputStream os = Files.newOutputStream(tmpPath)) {
            long count = 0;
            int n;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String hash;
            int bufferSize = 4 * 1024;
            byte[] buffer = new byte[bufferSize];
            while (EOF != (n = is.read(buffer))) {
                os.write(buffer, 0, n);
                digest.update(buffer, 0, n);
                count += n;
            }
            hash = Base64.getEncoder().encodeToString(digest.digest());
            Path resultFile = resolveFile(name);
            return new FilesystemStorageFile(Files.move(tmpPath, resultFile), Files.size(resultFile), hash);
        }
    }


    public void delete(List<UploadingEntity> existing) {
        existing.stream().filter(UploadingEntity::isUploaded).map(e -> e.getId().toString()).map(Paths::get).forEach(this::delete);
    }

    public void delete(UploadingEntity entity) {
        delete(resolveFile(entity.getId().toString()));
    }

    @SneakyThrows
    private void delete(Path path) {
        Files.deleteIfExists(path);
    }

    private Path resolveFile(String name) {
        return Paths.get(sc.getRoot()).resolve(Paths.get(name));
    }
}
