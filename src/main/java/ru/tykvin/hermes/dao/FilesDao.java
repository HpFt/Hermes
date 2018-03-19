package ru.tykvin.hermes.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.tykvin.hermes.configuration.StorageConfiguration;
import ru.tykvin.hermes.model.DownloadingEntity;
import ru.tykvin.hermes.model.FileInfo;
import ru.tykvin.hermes.model.UploadingEntity;
import ru.tykvin.hermes.model.User;
import ru.tykvin.hermes.tables.records.FilesUsersRecord;
import ru.tykvin.hermes.tables.records.VUnbindedFilesRecord;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.tykvin.hermes.Tables.FILES;
import static ru.tykvin.hermes.Tables.FILES_USERS;
import static ru.tykvin.hermes.Tables.V_FILE_INFO;
import static ru.tykvin.hermes.Tables.V_UNBINDED_FILES;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class FilesDao {
    private final FilesMapper mapper;
    private final DSLContext dslContext;
    private final StorageConfiguration sc;

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

    public Optional<DownloadingEntity> bindExistsToUser(UploadingEntity uploadingEntity, User user) {
        Optional<String> fileId = dslContext.selectFrom(FILES)
                .where(FILES.HASH.eq(uploadingEntity.getSha256()))
                .fetchOptional(FILES.ID);
        return fileId.map(id -> bindToUser(user, uploadingEntity, id).orElse(null));
    }

    public Optional<DownloadingEntity> bindToUser(User user, UploadingEntity file, String fid) {
        Optional<FilesUsersRecord> exists = dslContext.selectFrom(FILES_USERS)
                .where(
                        FILES_USERS.FILE_ID.eq(fid),
                        FILES_USERS.USER_ID.eq(user.getId().toString())
                ).fetchOptional();
        if (exists.isPresent()) {
            return findFileByBindingId(exists.get().getId());
        }
        String bindingId = dslContext.insertInto(FILES_USERS)
                .columns(
                        FILES_USERS.ID,
                        FILES_USERS.FILE_NAME,
                        FILES_USERS.USER_ID,
                        FILES_USERS.FILE_ID,
                        FILES_USERS.CREATE_AT,
                        FILES_USERS.EXPIRATION)
                .values(
                        UUID.randomUUID().toString(),
                        file.getFileName(),
                        user.getId().toString(),
                        fid,
                        OffsetDateTime.now(),
                        OffsetDateTime.now().plus(sc.getLifeTime()))
                .returning().fetchOne().get(FILES_USERS.ID);
        return findFileByBindingId(bindingId);
    }

    public Optional<DownloadingEntity> findFileByHash(String hash) {
        return dslContext.selectFrom(V_FILE_INFO)
                .where(V_FILE_INFO.HASH.eq(hash))
                .fetchOptional(mapper::mapToDownloadingEntity);
    }

    public Optional<DownloadingEntity> findFileByBindingId(String bindingId) {
        return dslContext.selectFrom(V_FILE_INFO)
                .where(V_FILE_INFO.BINDID.eq(bindingId))
                .fetchOptional(mapper::mapToDownloadingEntity);
    }

    public Optional<FileInfo> findFileInfo(UUID bindingId) {
        return dslContext.selectFrom(V_FILE_INFO)
                .where(V_FILE_INFO.BINDID.eq(bindingId.toString()))
                .fetchOptional(mapper::mapToFileInfo);
    }

    public List<FileInfo> findFilesByUser(User user) {
        return dslContext.selectFrom(V_FILE_INFO)
                .where(V_FILE_INFO.USERID.eq(user.getId().toString()))
                .and(V_FILE_INFO.EXPIRATION.greaterOrEqual(OffsetDateTime.now()))
                .fetch(mapper::mapToFileInfo);
    }

    public void clearExpiredBindings() {
        int count = dslContext.deleteFrom(FILES_USERS)
                .where(FILES_USERS.EXPIRATION.lessThan(OffsetDateTime.now()))
                .execute();
        log.info("Removed {} bindings ", count);
    }

    @Transactional
    public List<String> removeUnboundFiles() {
        List<String> unbound = dslContext.selectFrom(V_UNBINDED_FILES)
                .fetch(VUnbindedFilesRecord::getId);
        dslContext.deleteFrom(FILES)
                .where(FILES.ID.in(unbound))
                .execute();
        return unbound;
    }
}
