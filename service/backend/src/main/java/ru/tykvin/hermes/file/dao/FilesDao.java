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

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static ru.tykvin.hermes.Tables.FILES;
import static ru.tykvin.hermes.Tables.FILES_USERS;
import static ru.tykvin.hermes.Tables.V_FILE_INFO;

@Repository
@Transactional
@RequiredArgsConstructor
public class FilesDao {
    private final FilesMapper mapper;
    private final DSLContext dslContext;

    public Optional<FileInfo> findFileInfo(UUID id) {
        return dslContext.selectFrom(V_FILE_INFO)
                .where(V_FILE_INFO.ID.eq(id.toString()))
                .fetchOptional(mapper::mapToFileInfo);
    }

    public Optional<DownloadingEntity> createFile(UploadingEntity file, User user) {
        dslContext.insertInto(FILES)
                .columns(FILES.ID, FILES.HASH, FILES.CREATE_AT, FILES.SIZE)
                .values(
                        file.getId().toString(),
                        file.getSha256(),
                        file.getCreatAt(),
                        file.getSize()
                ).execute();
        return bindToUser(user, file, file.getId().toString());
    }

    public Optional<DownloadingEntity> findFileByHash(String hash) {
        return dslContext.selectFrom(V_FILE_INFO)
                .where(V_FILE_INFO.HASH.eq(hash))
                .fetchOptional(mapper::mapToDownloadingEntity);
    }

    public Optional<DownloadingEntity> findFileById(String id) {
        return dslContext.selectFrom(V_FILE_INFO)
                .where(V_FILE_INFO.FILEID.eq(id))
                .fetchOptional(mapper::mapToDownloadingEntity);
    }

    public Optional<DownloadingEntity> bindToUser(User user, UploadingEntity file, String fid) {
        Optional<FilesUsersRecord> exists = dslContext.selectFrom(FILES_USERS)
                .where(
                        FILES_USERS.FILE_ID.eq(fid),
                        FILES_USERS.USER_ID.eq(user.getId().toString())
                ).fetchOptional();
        if (exists.isPresent()) {
            return findFileById(exists.get().getFileId());
        }
        String fileId = dslContext.insertInto(FILES_USERS)
                .columns(
                        FILES_USERS.ID,
                        FILES_USERS.FILE_NAME,
                        FILES_USERS.USER_ID,
                        FILES_USERS.FILE_ID,
                        FILES_USERS.CREATE_AT,
                        FILES_USERS.EXPIRATION,
                        FILES_USERS.MAX_DOWNLOADS)
                .values(
                        UUID.randomUUID().toString(),
                        file.getFileName(),
                        user.getId().toString(),
                        fid,
                        OffsetDateTime.now(),
                        OffsetDateTime.now().plus(30, ChronoUnit.DAYS),
                        -1L)
                .returning().fetchOne().get(FILES_USERS.FILE_ID);
        return findFileById(fileId);
    }

    public Optional<DownloadingEntity> bindExistsToUser(UploadingEntity uploadingEntity, User user) {
        Optional<String> fileId = dslContext.selectFrom(FILES)
                .where(FILES.HASH.eq(uploadingEntity.getSha256()))
                .fetchOptional(FILES.ID);
        return fileId.map(id -> bindToUser(user, uploadingEntity, id).orElse(null));
    }
}
