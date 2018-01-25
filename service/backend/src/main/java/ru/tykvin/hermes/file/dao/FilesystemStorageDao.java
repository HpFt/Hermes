package ru.tykvin.hermes.file.dao;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Repository;
import ru.tykvin.hermes.file.configuration.StorageConfiguration;
import ru.tykvin.hermes.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FilesystemStorageDao implements StorageDao {

    private final StorageConfiguration sc;

    @Override
    @SneakyThrows
    public UUID save(User user, InputStream is) {
        UUID fileId = UUID.randomUUID();
        FileUtils.copyInputStreamToFile(is, Paths.get(sc.getRoot(), user.getId().toString(), fileId.toString()).toFile());
        return fileId;
    }

    @Override
    public OutputStream read(UUID fileId) {
        return null;
    }
}
