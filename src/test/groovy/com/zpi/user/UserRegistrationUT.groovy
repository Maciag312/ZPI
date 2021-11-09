package com.zpi.user

import com.zpi.api.common.dto.UserDTO
import com.zpi.domain.rest.ams.AmsService
import com.zpi.domain.rest.ams.AmsServiceImpl
import com.zpi.infrastructure.rest.ams.AmsClient
import com.zpi.infrastructure.rest.ams.AmsClientFallback
import com.zpi.testUtils.CommonFixtures
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Subject

class UserRegistrationUT extends Specification {
    def ams = Mock(AmsClient)
    def fallback = Mock(AmsClientFallback)

    @Subject
    private AmsService amsService = new AmsServiceImpl(ams, fallback)

    def "should create user"() {
        given:
            def user = CommonFixtures.userDTO().toDomain()

            ams.registerUser(_ as UserDTO) >> new ResponseEntity(HttpStatus.CREATED)

        when:
            def isSuccess = amsService.registerUser(user)

        then:
            isSuccess
    }

    def "should return conflict if user exists"() {
        given:
            def user = CommonFixtures.userDTO().toDomain()

            ams.registerUser(_ as UserDTO) >> new ResponseEntity<>(HttpStatus.CONFLICT)

        when:
            def isSuccess = amsService.registerUser(user)

        then:
            !isSuccess
    }
}
