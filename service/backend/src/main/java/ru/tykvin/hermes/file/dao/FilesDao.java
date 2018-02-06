package ru.tykvin.hermes.file.dao;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.tykvin.hermes.file.model.DownloadingEntity;
import ru.tykvin.hermes.file.model.FileInfo;
import ru.tykvin.hermes.file.model.FilesMapper;
import ru.tykvin.hermes.file.model.UploadingEntity;
import ru.tykvin.hermes.model.User;
import ru.tykvin.hermes.tables.records.FilesUsersRecord;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static ru.tykvin.hermes.Tables.FILES;
import static ru.tykvin.hermes.Tables.FILES_USERS;
import static ru.tykvin.hermes.Tables.V_FILE_INFO;

@Repository
@RequiredArgsConstructor
public class FilesDao {
    private final FilesMapper mapper;
    private final DSLContext dslContext;

    public Optional<FileInfo> findFileInfo(UUID id) {
        return dslContext.selectFrom(V_FILE_INFO)
                .where(V_FILE_INFO.ID.eq(id.toString()))
                .fetchOptional(mapper::mapToFileInfo);
    }

    @Transactional
    public void createFile(UploadingEntity file, User user) {
        dslContext.insertInto(FILES)
                .columns(FILES.ID, FILES.HASH, FILES.CREATE_AT, FILES.SIZE)
                .values(
                        file.getId().toString(),
                        file.getSha256(),
                        file.getCreatAt(),
                        file.getSize()
                ).execute();
        bindToUser(user, file.getId().toString());
    }

    public Optional<DownloadingEntity> findFileByHash(String hash) {
        return dslContext.selectFrom(FILES)
                .where(FILES.HASH.eq(hash))
                .fetchOptional(mapper::mapToDownloadingEntity);
    }

    public Optional<DownloadingEntity> findFileById(String id) {
        return dslContext.selectFrom(FILES)
                .where(FILES.ID.eq(id))
                .fetchOptional(mapper::mapToDownloadingEntity);
    }

    @Transactional
    public DownloadingEntity bindToUser(User user, String id) {
        Optional<FilesUsersRecord> exists = dslContext.selectFrom(FILES_USERS)
                .where(
                        FILES_USERS.FILE_ID.eq(id),
                        FILES_USERS.USER_ID.eq(user.getId().toString())
                ).fetchOptional();
        if (exists.isPresent()) {
            return findFileById(exists.get().getFileId()).orElse(null);
        }
        String fileId = dslContext.insertInto(FILES_USERS)
                .columns(
                        FILES_USERS.ID,
                        FILES_USERS.USER_ID,
                        FILES_USERS.FILE_ID,
                        FILES_USERS.CREATE_AT,
                        FILES_USERS.EXPIRATION,
                        FILES_USERS.MAX_DOWNLOADS)
                .values(
                        UUID.randomUUID().toString(),
                        user.getId().toString(),
                        id,
                        LocalDateTime.now(),
                        LocalDateTime.now().plus(30, ChronoUnit.DAYS),
                        -1L)
                .returning().fetchOne().get(FILES_USERS.FILE_ID);
        return findFileById(fileId).orElse(null);
    }

    @Transactional
    public boolean bindExistsToUser(UploadingEntity uploadingEntity, User user) {
        Optional<String> fileId = dslContext.selectFrom(FILES)
                .where(FILES.HASH.eq(uploadingEntity.getSha256()))
                .fetchOptional(FILES.ID);
        fileId.ifPresent(s -> bindToUser(user, s));
        return fileId.isPresent();
    }
}
