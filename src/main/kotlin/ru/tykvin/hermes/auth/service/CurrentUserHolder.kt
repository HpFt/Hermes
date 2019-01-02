package ru.tykvin.hermes.auth.service

import org.springframework.stereotype.Component
import ru.tykvin.hermes.auth.controller.User

@Component
class CurrentUserHolder {

    fun set(user: User) {
        USER_THREAD_LOCAL.set(user)
    }

    fun get(): User {
        return USER_THREAD_LOCAL.get()
    }

    companion object {
        private val USER_THREAD_LOCAL = ThreadLocal<User>()
    }
}
