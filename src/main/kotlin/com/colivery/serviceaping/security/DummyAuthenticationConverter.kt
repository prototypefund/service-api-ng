package com.colivery.serviceaping.security

import com.colivery.serviceaping.persistence.entity.UserEntity
import com.colivery.serviceaping.persistence.repository.UserRepository
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Profile("development")
@Component
class DummyAuthenticationConverter(
        private val userRepository: UserRepository
) : ServerAuthenticationConverter {

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val authHeader: String? = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)

        val user = when(authHeader) {
            null -> Mono.empty()
            else ->  Mono.justOrEmpty(this.userRepository.findByFirebaseUid(authHeader))
        }

        return user.map {
            PreAuthenticatedAuthenticationToken(it.firebaseUid, it)
        }
    }
}
