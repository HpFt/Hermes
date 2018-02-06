package ru.tykvin.hermes.file.storage;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
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
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.io.IOUtils.EOF;

@Service
@RequiredArgsConstructor
public class FilesystemStorage implements Storage {
    private final StorageConfiguration sc;
    private final FilesDao filesDao;
    private final FilesMapper mapper;


    public File read(UUID fileId) {
        return Paths.get(sc.getRoot(), fileId.toString()).toFile();
    }

    @Override
    @SneakyThrows
    public FilesystemStorageFile write(UploadingEntity uploadingEntity) {
        String tmpName = uploadingEntity.getId().toString();
        Path tmpPath = Paths.get(sc.getRoot()).resolve(tmpName);
        try (InputStream is = uploadingEntity.getItem().openStream(); OutputStream os = Files.newOutputStream(tmpPath)) {
            return writeToFilesystem(tmpPath, is, os);
        }
    }

    private FilesystemStorageFile writeToFilesystem(Path path, InputStream is, OutputStream os) throws NoSuchAlgorithmException, IOException {
        long count = 0;
        int n;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        FileUtils.touch(path.toFile());
        String hash = "";
        int bufferSize = 4 * 1024;
        byte[] buffer = new byte[bufferSize];
        while (EOF != (n = is.read(buffer))) {
            os.write(buffer, 0, n);
            digest.update(buffer, 0, n);
            count += n;
        }
        hash = new BASE64Encoder().encode(digest.digest());
        return new FilesystemStorageFile(path, count * bufferSize, hash);
    }


    public void delete(List<UploadingEntity> existing) {
        existing.stream().filter(UploadingEntity::isUploaded).map(e -> e.getId().toString()).map(Paths::get).forEach(this::delete);
    }

    @SneakyThrows
    private void delete(Path path) {
        Files.deleteIfExists(path);
    }

    public void delete(UploadingEntity entity) {
        delete(Paths.get(entity.getId().toString()));
    }
}
