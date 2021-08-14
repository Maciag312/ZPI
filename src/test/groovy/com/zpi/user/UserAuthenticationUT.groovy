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
            def user = CommonFixtures.userDTO()
            def hashedUser = user.toHashedDomain()
            userRepository.getByKey(hashedUser.getLogin()) >> Optional.of(hashedUser)

        when:
            def isAuthenticated = userService.isAuthenticated(user)

        then:
            isAuthenticated
    }

    def "should return false when user does not exist"() {
        given:
            def user = CommonFixtures.userDTO()
            def hashedUser = user.toHashedDomain()
            userRepository.getByKey(hashedUser.getLogin()) >> Optional.empty()

        when:
            def isAuthenticated = userService.isAuthenticated(user)

        then:
            !isAuthenticated
    }

    def "should return false when credentials does not match"() {
        given:
            def user = CommonFixtures.userDTO()
            def user2 = UserDTO.builder()
                    .login(user.getLogin())
                    .password(user.getPassword() + "asdf")
                    .build()

            def hashedUser = user.toHashedDomain()
            def hashedUser2 = user2.toHashedDomain()
            userRepository.getByKey(hashedUser.getLogin()) >> Optional.of(hashedUser2)

        when:
            def isAuthenticated = userService.isAuthenticated(user)

        then:
            !isAuthenticated
    }
}
