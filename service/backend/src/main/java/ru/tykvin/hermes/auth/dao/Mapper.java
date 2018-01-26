package ru.tykvin.hermes.auth.dao;

import org.springframework.stereotype.Component;
import ru.tykvin.hermes.model.User;
import ru.tykvin.hermes.tables.records.UsersRecord;

import java.util.UUID;

@Component
public class Mapper {
    public User mapToUser(UsersRecord usersRecord) {
        return new User(UUID.fromString(usersRecord.getId()), usersRecord.getCreateAt(), usersRecord.getIp());
    }

}
