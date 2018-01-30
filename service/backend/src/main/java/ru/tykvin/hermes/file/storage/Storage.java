package ru.tykvin.hermes.file.storage;

import ru.tykvin.hermes.model.User;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public interface Storage {

    List<FileInfo> save(User user, HttpServletRequest file);
    OutputStream read(UUID fileId);

}
