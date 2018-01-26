package ru.tykvin.hermes.file.storage;

import ru.tykvin.hermes.model.User;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.util.UUID;

public interface Storage {

    UUID save(User user, HttpServletRequest file);
    OutputStream read(UUID fileId);

}
