package ru.tykvin.hermes.auth.dao;

import credit.station.jooq.tables.records.UsersRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.tykvin.hermes.model.User;

import java.util.Optional;
import java.util.UUID;

import static credit.station.jooq.tables.Users.USERS;

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

    public void updateUserIp(UUID id, String currentIp) {
        dslContext.update(USERS)
                .set(USERS.IP, currentIp)
                .where(USERS.ID.eq(id.toString()))
                .execute();
    }
}

