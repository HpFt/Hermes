package ru.tykvin.hermes.file.storage;

import ru.tykvin.hermes.file.model.DownloadingEntity;
import ru.tykvin.hermes.model.User;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface Storage {

    List<DownloadingEntity> save(User user, HttpServletRequest file);
    File read(UUID fileId);

}
