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

class UserAuthenticationUT extends Specification {
    def ams = Mock(AmsClient)
    def fallback = Mock(AmsClientFallback)

    @Subject
    private AmsService service = new AmsServiceImpl(ams, fallback)

    def "should return true when credentials match"() {
        given:
            def user = CommonFixtures.userDTO().toDomain()
            ams.authenticate(_ as UserDTO) >> new ResponseEntity<>(HttpStatus.OK)

        when:
            def isAuthenticated = service.isAuthenticated(user)

        then:
            isAuthenticated
    }

    def "should return false when user credentials are not correct"() {
        given:
            def user = CommonFixtures.userDTO().toDomain()
            ams.authenticate(_ as UserDTO) >> new ResponseEntity<>(HttpStatus.UNAUTHORIZED)

        when:
            def isAuthenticated = service.isAuthenticated(user)

        then:
            !isAuthenticated
    }
}
