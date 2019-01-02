package ru.tykvin.hermes.auth.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.tykvin.hermes.auth.controller.TokenData
import ru.tykvin.hermes.auth.controller.User
import ru.tykvin.hermes.configuration.AuthConfiguration
import java.time.OffsetDateTime
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

@Service
open class TokenService(
        private val cfg: AuthConfiguration,
        private val userRepository: UserRepository,
        private val om: ObjectMapper
) {

    @Transactional
    open fun signiIn(ip: String): User {
        return User(UUID.randomUUID(), OffsetDateTime.now(), "")//userRepository.findUserByIp(ip).orElseGet { createUser(ip) }
    }

    private fun createUser(ip: String): User {
        return userRepository.save(User(UUID.randomUUID(), OffsetDateTime.now(), ip))
    }

    fun validateToken(tokenData: TokenData, currentIp: String): Boolean {
        val user = tokenData.user
        return user.ip == currentIp
    }

    fun getToken(cipher: String): TokenData {
        return decrypt(cipher)
    }

    fun updateUserIp(user: User, remoteAddr: String): User {
        return userRepository.save(
                userRepository.findById(user.id)
                        .safe()
                        ?.also { it.copy(ip = remoteAddr) }
                        ?: throw IllegalArgumentException("User not found: $user")
        )
    }

    fun createToken(user: User): String {
        return encrypt(TokenData(user))
    }

    private fun encrypt(token: TokenData): String {
        try {
            val signatureAlgorithm = SignatureAlgorithm.HS256
            val apiKeySecretBytes = DatatypeConverter.parseBase64Binary(cfg!!.secret)
            val signingKey = SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName())
            return Jwts.builder()
                    .setPayload(om.writeValueAsString(token))
                    .signWith(signatureAlgorithm, signingKey)
                    .compact()
        } catch (e: Exception) {
            throw IllegalArgumentException(e.message)
        }

    }

    private fun decrypt(token: String): TokenData {
        try {
            val json = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(cfg!!.secret))
                    .parse(token).body
            return om.convertValue(json, TokenData::class.java)
        } catch (e: Exception) {
            throw IllegalArgumentException(e.message)
        }

    }
}


fun <T> Optional<T>.safe(): T? = this.orElseGet { null }