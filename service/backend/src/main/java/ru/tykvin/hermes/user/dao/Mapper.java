package ru.tykvin.hermes.user.dao;

import credit.station.jooq.tables.records.UsersRecord;
import org.springframework.stereotype.Component;
import ru.tykvin.hermes.model.TokenData;
import ru.tykvin.hermes.model.User;

import java.util.UUID;

@Component
public class Mapper {
    public User mapToUser(UsersRecord usersRecord) {
        return new User(UUID.fromString(usersRecord.getId()), usersRecord.getCreateAt(), usersRecord.getIp());
    }

}
