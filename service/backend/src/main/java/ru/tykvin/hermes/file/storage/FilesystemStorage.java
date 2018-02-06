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

    @Override
    @SneakyThrows
    public List<DownloadingEntity> save(User user, HttpServletRequest request) {
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
                prepared.
                final UUID fileId = UUID.randomUUID();
                final Path root = Paths.get(sc.getRoot());
                DownloadingEntity result = writeTmpFile(item, root, fileId);
                results.add(result);
            }
        }
        return results.stream().map(result -> saveFileInfo(result, user)).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    @SneakyThrows
    private DownloadingEntity saveFileInfo(DownloadingEntity result, User user) {
        Optional<DownloadingEntity> filInfo = filesDao.findFileByHash(result.getSha256());
        if (!filInfo.isPresent()) {
            Path path = result.getPath().resolveSibling(result.getId().toString());
            Files.move(result.getPath(), path);
            result = filesDao.createFile(new DownloadingEntity(
                    result.getId(),
                    result.getSize(),
                    LocalDateTime.now(),
                    result.getSha256(),
                    mapper.createUrl(path),
                    result.getFileName(),
                    path));
        } else {
            Files.delete(result.getPath());
            result = filInfo.get();
        }
        return filesDao.bindToUser(user, result);
    }

    @Override
    public File read(UUID fileId) {
        return Paths.get(sc.getRoot(), fileId.toString()).toFile();
    }

    @SneakyThrows
    public void uploadTmp(UploadingEntity uploadingEntity) {
        String tmpName = uploadingEntity.getId().toString() + ".tmp";
        Path tmpPath = Paths.get(sc.getRoot()).resolve(tmpName);
        try (InputStream is = uploadingEntity.getItem().openStream(); OutputStream os = Files.newOutputStream(tmpPath)) {
            FilesystemStorageFile file = writeToFilesystem(tmpPath, is, os);
            uploadingEntity.setSize(file.getSize());
            uploadingEntity.setSha256(file.getHash());
            uploadingEntity.setTmp(true);
            uploadingEntity.setUploaded(true);
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
}
