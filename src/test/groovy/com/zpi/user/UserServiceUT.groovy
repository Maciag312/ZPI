package com.zpi.user

import com.zpi.user.api.UserDTO
import com.zpi.user.domain.EndUserRepository
import com.zpi.user.domain.EndUserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Subject

class UserServiceUT extends Specification {
    def userRepository = Mock(EndUserRepository)

    @Subject
    private EndUserService userService = new EndUserService(userRepository)

    def "should create user"() {
        given:
            def user = createSampleUser()

            def hashedUser = user.toHashedDomain()

            userRepository.getByKey(hashedUser.getLogin()) >> Optional.empty()

        when:
            def response = userService.createUser(user)

        then:
            response == new ResponseEntity<>(HttpStatus.CREATED)
    }

    def "should return conflict if user exists"() {
        given:
            def user = createSampleUser()

            def hashedUser = user.toHashedDomain()
            userRepository.getByKey(hashedUser.getLogin()) >> Optional.of(hashedUser)

        when:
            def response = userService.createUser(user)

        then:
            response == new ResponseEntity<>(HttpStatus.CONFLICT)
    }

    private static UserDTO createSampleUser() {
        return UserDTO.builder()
                .login("Login")
                .password("Password")
                .build()
    }
}
