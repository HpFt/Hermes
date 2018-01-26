package ru.tykvin.hermes.file.storage;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.file.configuration.StorageConfiguration;
import ru.tykvin.hermes.model.User;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilesystemStorage implements Storage {

    private final StorageConfiguration sc;

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
                final Path path = Paths.get(sc.getRoot(), user.getId().toString(), fileId.toString());
                write(item, path);
            }
        }
        return null;
    }

    private void write(FileItemStream item, Path path) throws IOException {
        try {
            FileUtils.copyInputStreamToFile(item.openStream(), path.toFile());
        } catch (Exception e) {
            FileUtils.forceDelete(path.toFile());
        }
    }

    @Override
    public OutputStream read(UUID fileId) {
        return null;
    }
}
