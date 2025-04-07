package com.ipl.ipl.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtUtil.getTokenFromRequest(request)

        if (!token.isNullOrBlank()) {
            val username = jwtUtil.extractUsername(token)
            val roles = jwtUtil.extractRoles(token)
            val rolesList = roles.map { it }

            if (SecurityContextHolder.getContext().authentication == null) {
                val authorities = rolesList.map { SimpleGrantedAuthority("ROLE_$it") }
                val authentication = UsernamePasswordAuthenticationToken(username, null, authorities)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }


}
