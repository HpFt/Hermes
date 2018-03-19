package ru.tykvin.hermes.dao;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.model.User;
import ru.tykvin.hermes.tables.records.UsersRecord;

import java.util.UUID;

@Service
public class UserMapper {
    public User mapToUser(UsersRecord usersRecord) {
        return new User(UUID.fromString(usersRecord.getId()), usersRecord.getCreateAt(), usersRecord.getIp());
    }

}
