package ru.tykvin.hermes.file.dao;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.tykvin.hermes.file.model.FileInfo;
import ru.tykvin.hermes.file.model.DownloadingEntity;
import ru.tykvin.hermes.file.model.FilesMapper;
import ru.tykvin.hermes.model.User;
import ru.tykvin.hermes.tables.records.FilesRecord;
import ru.tykvin.hermes.tables.records.FilesUsersRecord;

import static ru.tykvin.hermes.Tables.FILES;
import static ru.tykvin.hermes.Tables.FILES_USERS;
import static ru.tykvin.hermes.Tables.V_FILE_INFO;

@Repository
@RequiredArgsConstructor
public class FilesDao {
    private final FilesMapper mapper;
    private final DSLContext dslContext;

    public FileInfo findFileInfo(UUID id) {
        dslContext.selectFrom(V_FILE_INFO)
                .where(V_FILE_INFO.ID.eq(id.toString()))
                .fetchOptional(mapper::mapToFileInfo);
    }



    public DownloadingEntity createFile(DownloadingEntity file) {
        FilesRecord record = dslContext.insertInto(FILES)
            .columns(FILES.ID, FILES.HASH, FILES.PATH, FILES.CREATE_AT, FILES.SIZE)
            .values(
                file.getId().toString(),
                file.getSha256(),
                file.getPath().toString(),
                file.getCreateAt(),
                file.getSize()
            ).returning().fetchOne();
        return mapper.mapToDownloadingEntity(record);
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
    public DownloadingEntity bindToUser(User user, DownloadingEntity result) {
        Optional<FilesUsersRecord> exists = dslContext.selectFrom(FILES_USERS)
            .where(
                FILES_USERS.FILE_ID.eq(result.getId().toString()),
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
                result.getId().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plus(30, ChronoUnit.DAYS),
                -1L)
            .returning().fetchOne().get(FILES_USERS.FILE_ID);
        return findFileById(fileId).orElse(null);
    }
}
