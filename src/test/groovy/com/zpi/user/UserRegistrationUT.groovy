package com.zpi.user

import com.zpi.api.common.dto.UserDTO
import com.zpi.domain.rest.ams.AmsService
import com.zpi.domain.rest.ams.AmsServiceImpl
import com.zpi.infrastructure.rest.ams.AmsClient
import com.zpi.testUtils.CommonFixtures
import spock.lang.Specification
import spock.lang.Subject

class UserRegistrationUT extends Specification {
    def ams = Mock(AmsClient)

    @Subject
    private AmsService amsService = new AmsServiceImpl(ams)

    def "should create user"() {
        given:
            def user = CommonFixtures.userDTO().toHashedDomain()

            ams.registerUser(_ as UserDTO) >> true

        when:
            def isSuccess = amsService.registerUser(user)

        then:
            isSuccess
    }

    def "should return conflict if user exists"() {
        given:
            def user = CommonFixtures.userDTO().toHashedDomain()

            ams.registerUser(new UserDTO(user.getLogin(), user.getPassword())) >> false

        when:
            def isSuccess = amsService.registerUser(user)

        then:
            !isSuccess
    }
}
