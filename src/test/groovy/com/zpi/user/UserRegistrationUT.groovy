package com.zpi.user

import com.zpi.CommonFixtures
import com.zpi.user.domain.UserRepository
import com.zpi.user.domain.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Subject

class UserRegistrationUT extends Specification {
    def userRepository = Mock(UserRepository)

    @Subject
    private UserService userService = new UserService(userRepository)

    def "should create user"() {
        given:
            def user = CommonFixtures.userDTO()

            def hashedUser = user.toHashedDomain()

            userRepository.getByKey(hashedUser.getLogin()) >> Optional.empty()

        when:
            def response = userService.createUser(user)

        then:
            response == new ResponseEntity<>(HttpStatus.CREATED)
    }

    def "should return conflict if user exists"() {
        given:
            def user = CommonFixtures.userDTO()

            def hashedUser = user.toHashedDomain()
            userRepository.getByKey(hashedUser.getLogin()) >> Optional.of(hashedUser)

        when:
            def response = userService.createUser(user)

        then:
            response == new ResponseEntity<>(HttpStatus.CONFLICT)
    }
}
