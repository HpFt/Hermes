package ru.tykvin.hermes.security;

import org.springframework.stereotype.Component;
import ru.tykvin.hermes.model.User;

@Component
public class CurrentUserHolder {
    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();
    public void set(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    public User get() {
        return USER_THREAD_LOCAL.get();
    }
}
