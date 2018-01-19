package ru.tykvin.hermes.user.dao;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.tykvin.hermes.model.User;

import java.util.UUID;

import static credit.station.jooq.tables.Tokens.TOKENS;
import static credit.station.jooq.tables.Users.USERS;

@Repository
@RequiredArgsConstructor
public class UserDao {

    private final DSLContext dslContext;

    public void createUser(User user) {
        String userID = UUID.randomUUID().toString();
        dslContext.insertInto(USERS)
                .columns(USERS.ID, USERS.IP)
                .values(userID, user.getIp())
                .execute();
    }


    public void saveToken(User existsUser, String cipherToken) {
        dslContext.insertInto(TOKENS)
                .columns(TOKENS.TOKEN, TOKENS.USER_ID)
                .values(cipherToken, existsUser.getId().toString())
                .execute();
    }

}

