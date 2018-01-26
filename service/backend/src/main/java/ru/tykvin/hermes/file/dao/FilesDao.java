package ru.tykvin.hermes.file.dao;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.tykvin.hermes.file.storage.FileInfo;
import ru.tykvin.hermes.tables.records.FilesRecord;

import java.util.Optional;

import static ru.tykvin.hermes.Tables.FILES;

@Repository
@RequiredArgsConstructor
public class FilesDao {

    private final FilesMapper mapper;
    private final DSLContext dslContext;

    public FileInfo createFile(FileInfo file) {
        FilesRecord record = dslContext.insertInto(FILES)
                .columns(FILES.ID, FILES.HASH, FILES.PATH, FILES.CREATE_AT, FILES.SIZE)
                .values(
                        file.getId().toString(),
                        file.getSha256(),
                        file.getPath().toString(),
                        file.getCreateAt(),
                        file.getSize()
                ).returning().fetchOne();
        return mapper.mapToFileInfo(record);
    }

    public Optional<FileInfo> findFileByHash(String hash) {
        return dslContext.selectFrom(FILES)
                .where(FILES.HASH.eq(hash))
                .fetchOptional(mapper::mapToFileInfo);
    }

}
