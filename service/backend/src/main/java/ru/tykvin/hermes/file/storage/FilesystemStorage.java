package ru.tykvin.hermes.file.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.tykvin.hermes.auth.dao.Mapper;
import ru.tykvin.hermes.file.configuration.StorageConfiguration;
import ru.tykvin.hermes.file.dao.FilesDao;
import ru.tykvin.hermes.file.dao.FilesMapper;
import ru.tykvin.hermes.model.User;
import sun.misc.BASE64Encoder;

import static org.apache.commons.io.IOUtils.EOF;

@Service
@RequiredArgsConstructor
public class FilesystemStorage implements Storage {
    private final StorageConfiguration sc;
    private final FilesDao filesDao;
    private final FilesMapper mapper;

    @Override
    @SneakyThrows
    public List<FileInfo> save(User user, HttpServletRequest request) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("It's not multipart");
        }
        ServletFileUpload fileUpload = new ServletFileUpload();
        FileItemIterator iterator = fileUpload.getItemIterator(request);
        List<FileInfo> results = new ArrayList<>();
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();
            if (!item.isFormField()) {
                final UUID fileId = UUID.randomUUID();
                final Path root = Paths.get(sc.getRoot());
                FileInfo result = writeTmpFile(item, root, fileId);
                results.add(result);
            }
        }
        return results.stream().map(result -> saveFileInfo(result, user)).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    @SneakyThrows
    private FileInfo saveFileInfo(FileInfo result, User user) {
        Optional<FileInfo> filInfo = filesDao.findFileByHash(result.getSha256());
        if (!filInfo.isPresent()) {
            Path path = result.getPath().resolveSibling(result.getId().toString());
            Files.move(result.getPath(), path);
            result = filesDao.createFile(new FileInfo(
                result.getId(),
                result.getSize(),
                LocalDateTime.now(),
                result.getSha256(),
                mapper.createUrl(path),
                path));
        } else {
            Files.delete(result.getPath());
            result = filInfo.get();
        }
        return filesDao.bindToUser(user, result);
    }

    @Override
    public OutputStream read(UUID fileId) {
        return null;
    }

    @SneakyThrows
    private FileInfo writeTmpFile(FileItemStream item, Path root, UUID fileId) {
        long count = 0;
        int n;
        final String tmpName = fileId.toString() + ".tmp";
        Path tmpPath = root.resolve(tmpName);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        FileUtils.touch(tmpPath.toFile());
        String hash = "";
        int bufferSize = 4 * 1024;
        try (InputStream is = item.openStream(); OutputStream os = Files.newOutputStream(tmpPath)) {
            byte[] buffer = new byte[bufferSize];
            while (EOF != (n = is.read(buffer))) {
                os.write(buffer, 0, n);
                digest.update(buffer, 0, n);
                count += n;
            }
            hash = new BASE64Encoder().encode(digest.digest());
        }
        return new FileInfo(fileId, count * bufferSize, LocalDateTime.now(), hash, "", tmpPath);
    }
}
