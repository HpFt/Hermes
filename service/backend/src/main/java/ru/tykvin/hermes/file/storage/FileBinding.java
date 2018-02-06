package ru.tykvin.hermes.file.storage;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.tykvin.hermes.file.model.DownloadingEntity;
import ru.tykvin.hermes.model.User;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class FileBinding {
    private final DownloadingEntity downloadingEntity;
    private final User user;
    private final FileBindingConstraints constraints;
    private final LocalDateTime createAt;
}
