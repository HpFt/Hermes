package ru.tykvin.hermes.file.storage;

import lombok.SneakyThrows;
import ru.tykvin.hermes.file.model.DownloadingEntity;
import ru.tykvin.hermes.file.model.FilesystemStorageFile;
import ru.tykvin.hermes.file.model.UploadingEntity;
import ru.tykvin.hermes.model.User;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface Storage {

    @SneakyThrows
    FilesystemStorageFile write(UploadingEntity uploadingEntity);
}
