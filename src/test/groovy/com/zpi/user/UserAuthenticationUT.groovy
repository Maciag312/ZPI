package com.zpi.user

import com.zpi.CommonFixtures
import com.zpi.common.api.dto.UserDTO
import com.zpi.user.domain.UserRepository
import com.zpi.user.domain.UserService
import spock.lang.Specification
import spock.lang.Subject

class UserAuthenticationUT extends Specification {
    def userRepository = Mock(UserRepository)

    @Subject
    private UserService userService = new UserService(userRepository)

    def "should return true when credentials match"() {
        given:
            def user = CommonFixtures.userDTO().toHashedDomain()
            userRepository.getByKey(user.getLogin()) >> Optional.of(user)

        when:
            def isAuthenticated = userService.isAuthenticated(user)

        then:
            isAuthenticated
    }

    def "should return false when user does not exist"() {
        given:
            def user = CommonFixtures.userDTO().toHashedDomain()
            userRepository.getByKey(user.getLogin()) >> Optional.empty()

        when:
            def isAuthenticated = userService.isAuthenticated(user)

        then:
            !isAuthenticated
    }

    def "should return false when credentials does not match"() {
        given:
            def user = CommonFixtures.userDTO().toHashedDomain()
            def savedUser = UserDTO.builder()
                    .login(user.getLogin())
                    .password(user.getPassword() + "asdf")
                    .build().toHashedDomain()

            userRepository.getByKey(user.getLogin()) >> Optional.of(savedUser)

        when:
            def isAuthenticated = userService.isAuthenticated(user)

        then:
            !isAuthenticated
    }
}
