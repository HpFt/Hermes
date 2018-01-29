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
import ru.tykvin.hermes.model.User;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.apache.commons.io.IOUtils.EOF;

@Service
@RequiredArgsConstructor
public class FilesystemStorage implements Storage {

    private final StorageConfiguration sc;
    private final FilesDao filesDao;

    @Override
    @SneakyThrows
    public UUID save(User user, HttpServletRequest request) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("It's not multipart");
        }
        ServletFileUpload fileUpload = new ServletFileUpload();
        FileItemIterator iterator = fileUpload.getItemIterator(request);
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();
            if (!item.isFormField()) {
                final UUID fileId = UUID.randomUUID();
                final Path root = Paths.get(sc.getRoot(), user.getId().toString());
                FileInfo result = write(item, root, fileId);
                filesDao.createFile(result);
            }
        }
        return null;
    }

    @Override
    public OutputStream read(UUID fileId) {
        return null;
    }

    private FileInfo write(FileItemStream item, Path root, UUID fileId) throws IOException, NoSuchAlgorithmException {
        long count = 0;
        int n;
        final String tmpName = fileId.toString() + ".tmp";
        Path tmpPath = root.resolve(tmpName);
        Path path = root.resolve(fileId.toString());
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        InputStream is = item.openStream();
        FileUtils.touch(tmpPath.toFile());
        OutputStream os = Files.newOutputStream(tmpPath);
        String hash = "";
        int bufferSize = 4 * 1024;
        try {
            byte[] buffer = new byte[bufferSize];
            while (EOF != (n = is.read(buffer))) {
                os.write(buffer, 0, n);
                digest.update(buffer, 0, n);
                count += n;
            }
            Files.move(tmpPath, path);
            hash = new BASE64Encoder().encode(digest.digest());
        } catch (Exception e) {
            e.printStackTrace();
            path = tmpPath;
        }
        return new FileInfo(fileId, count * bufferSize, LocalDateTime.now(), hash, path);
    }

}
