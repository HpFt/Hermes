package ru.tykvin.hermes.auth.dao;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.tykvin.hermes.model.User;
import ru.tykvin.hermes.tables.records.UsersRecord;

import java.util.Optional;
import java.util.UUID;

import static ru.tykvin.hermes.Tables.USERS;

@Repository
@RequiredArgsConstructor
public class AuthDao {

    private final DSLContext dslContext;
    private final Mapper mapper;

    public User createUser(User user) {
        String userID = UUID.randomUUID().toString();
        UsersRecord record = dslContext.insertInto(USERS)
                .columns(USERS.ID, USERS.IP)
                .values(userID, user.getIp())
                .returning()
                .fetchOne();
        return mapper.mapToUser(record);
    }

    public Optional<User> findUserByIp(String ip) {
        return dslContext.selectFrom(USERS)
                .where(USERS.IP.eq(ip))
                .fetchOptional(mapper::mapToUser);

    }

    public User updateUserIp(UUID id, String currentIp) {
        UsersRecord record = dslContext.update(USERS)
                .set(USERS.IP, currentIp)
                .where(USERS.ID.eq(id.toString()))
                .returning()
                .fetchOne();
        return mapper.mapToUser(record);
    }
}

