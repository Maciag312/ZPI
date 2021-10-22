package com.zpi.user

import com.zpi.api.common.dto.UserDTO
import com.zpi.domain.user.UserRepository
import com.zpi.testUtils.MvcRequestHelpers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class UserRegistrationFT extends Specification {
    @Autowired
    private UserRepository repository

    @Autowired
    private MvcRequestHelpers commonHelpers

    private static final String baseUri = "/api/user/register"

    def cleanup() {
        repository.clear()
    }

    def "should register new user"() {
        given:
            def user = Fixtures.userWithRandomData()

        when:
            def request = commonHelpers.postRequest(user, baseUri)

        then:
            request.andExpect(status().isCreated())

        and:
            def hashedDomain = user.toHashedDomain()
            def result = repository.findByKey(hashedDomain.getLogin()).get()

            result.login == hashedDomain.login
            result.password == hashedDomain.password
    }

    def "should return conflict on existing user"() {
        given:
            def user = Fixtures.userWithRandomData()

        when:
            commonHelpers.postRequest(user, baseUri)
            def request = commonHelpers.postRequest(user, baseUri)

        then:
            request.andExpect(status().isConflict())

        and:
            def hashedDomain = user.toHashedDomain()
            def result = repository.findByKey(hashedDomain.getLogin()).get()

            result.login == hashedDomain.login
            result.password == hashedDomain.password
    }

    def "should return conflict on login crash"() {
        given:
            def userA = Fixtures.userWithRandomData()

            def userB = UserDTO.builder()
                    .login(userA.getLogin())
                    .password("fdsa")
                    .build()

        when:
            commonHelpers.postRequest(userA, baseUri)
            def request = commonHelpers.postRequest(userB, baseUri)

        then:
            request.andExpect(status().isConflict())

        and:
            def hashedDomain = userA.toHashedDomain()
            def result = repository.findByKey(hashedDomain.getLogin()).get()

            result.login == hashedDomain.login
            result.password == hashedDomain.password
    }

    def "should return bad request on null user"() {
        given:
            def user = null

        when:
            def request = commonHelpers.postRequest(user, baseUri)

        then:
            request.andExpect(status().isBadRequest())

    }

    def "should return bad request on malformed user"() {
        given:
            def userA = UserDTO.builder().build()
            def userB = UserDTO.builder().login("Login").build()

        when:
            def requestA = commonHelpers.postRequest(userA, baseUri)
            def requestB = commonHelpers.postRequest(userB, baseUri)

        then:
            requestA.andExpect(status().isBadRequest())
            requestB.andExpect(status().isBadRequest())

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
