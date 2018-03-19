package ru.tykvin.hermes.dao;

import ru.tykvin.hermes.model.StorageFile;
import ru.tykvin.hermes.model.UploadingEntity;
import ru.tykvin.hermes.model.UploadingProgress;

public interface Storage {

    StorageFile write(UploadingEntity uploadingEntity, UploadingProgress progress);
}
