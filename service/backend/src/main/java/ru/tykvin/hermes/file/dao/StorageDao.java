package ru.tykvin.hermes.file.dao;

import ru.tykvin.hermes.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public interface StorageDao {

    UUID save(User user, InputStream is);
    OutputStream read(UUID fileId);

}
