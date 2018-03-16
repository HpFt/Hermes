package ru.tykvin.hermes.file.storage;

import lombok.SneakyThrows;
import ru.tykvin.hermes.file.model.FilesystemStorageFile;
import ru.tykvin.hermes.file.model.UploadingEntity;

public interface Storage {

    FilesystemStorageFile write(UploadingEntity uploadingEntity);
}
