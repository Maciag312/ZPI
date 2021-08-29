package com.zpi.authCode.consentRequest

import com.zpi.domain.authCode.consentRequest.AuthCode
import com.zpi.domain.authCode.consentRequest.authCodeIssuer.AuthCodeIssuer
import com.zpi.domain.authCode.consentRequest.authCodeIssuer.AuthCodeIssuerImpl
import com.zpi.domain.authCode.consentRequest.authCodeIssuer.AuthCodeRepository
import spock.lang.Specification
import spock.lang.Subject

class AuthCodeIssuerUT extends Specification {
    def repository = Mock(AuthCodeRepository)

    @Subject
    private AuthCodeIssuer issuer = new AuthCodeIssuerImpl(repository)

    def "should return and save authCode in repository"() {
        given:
            repository.save(_ as String, _ as AuthCode) >> null

        when:
            def result = issuer.issue()

        then:
            1 * repository.save(_ as String, _ as AuthCode)

        and:
            !result.getValue().isEmpty()
    }
}
