package ru.tykvin.hermes.auth.service

import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.tykvin.hermes.auth.controller.User
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthFilter(
        private val tokenService: TokenService,
        private val currentUserHolder: CurrentUserHolder
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        var user: User
        val xAuth = request.getHeader(TOKEN_NAME)
        val remoteAddr = request.remoteAddr
        try {
            val tokenData = tokenService.getToken(xAuth)
            user = tokenData.user
            if (!tokenService.validateToken(tokenData, remoteAddr)) {
                user = tokenService.updateUserIp(user, remoteAddr)
                updateToken(user, response)
            }
        } catch (ignored: Exception) {
            user = tokenService.signiIn(remoteAddr)
            updateToken(user, response)
        }

        currentUserHolder.set(user)
        filterChain.doFilter(request, response)
    }

    private fun updateToken(user: User, response: HttpServletResponse) {
        response.setHeader(TOKEN_NAME, tokenService.createToken(user))
    }

    companion object {
        private const val TOKEN_NAME = "token"
    }

}
