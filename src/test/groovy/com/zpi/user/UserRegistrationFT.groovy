package com.zpi.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.zpi.user.api.UserDTO
import com.zpi.user.domain.EndUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class UserRegistrationFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private EndUserRepository repository

    @Autowired
    private ObjectMapper mapper

    def "should register new user"() {
        given:
            def user = Fixtures.userWithRandomData()

        when:
            def request = registerUserRequest(user)

        then:
            request.andExpect(status().isCreated())

        and:
            def hashedDomain = user.toHashedDomain()
            def result = repository.getByKey(hashedDomain.getLogin()).get()

            result == hashedDomain
    }

    def "should return conflict on existing user"() {
        given:
            def user = Fixtures.userWithRandomData()

        when:
            registerUserRequest(user)
            def request = registerUserRequest(user)

        then:
            request.andExpect(status().isConflict())

        and:
            def hashedDomain = user.toHashedDomain()
            def result = repository.getByKey(hashedDomain.getLogin()).get()

            result == hashedDomain
    }

    def "should return conflict on login crash"() {
        given:
            def userA = Fixtures.userWithRandomData()

            def userB = UserDTO.builder()
                    .login(userA.getLogin())
                    .password("")
                    .build()

        when:
            registerUserRequest(userA)
            def request = registerUserRequest(userB)

        then:
            request.andExpect(status().isConflict())

        and:
            def hashedDomain = userA.toHashedDomain()
            def result = repository.getByKey(hashedDomain.getLogin()).get()

            result == hashedDomain
    }

    def "should return bad request on null user"() {
        given:
            def user = null

        when:
            def request = registerUserRequest(user)

        then:
            request.andExpect(status().isBadRequest())
    }

    def "should return bad request on malformed user"() {
        given:
            def userA = UserDTO.builder().build()
            def userB = UserDTO.builder().login("Login").build()

        when:
            def requestA = registerUserRequest(userA)
            def requestB = registerUserRequest(userB)

        then:
            requestA.andExpect(status().isBadRequest())
            requestB.andExpect(status().isBadRequest())

    }

    private ResultActions registerUserRequest(UserDTO user) {
        return mockMvc.perform(
                post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
        )
    }

    private class Fixtures {
        static UserDTO userWithRandomData() {
            def login = UUID.randomUUID().toString()
            def password = UUID.randomUUID().toString()

            return UserDTO.builder()
                    .login(login)
                    .password(password)
                    .build()
        }
    }
}
