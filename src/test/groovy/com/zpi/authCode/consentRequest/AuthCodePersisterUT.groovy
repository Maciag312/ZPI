package com.zpi.authCode.consentRequest

import com.zpi.domain.authCode.consentRequest.AuthCode
import com.zpi.domain.authCode.consentRequest.AuthUserData
import com.zpi.domain.authCode.consentRequest.TicketData
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodePersister
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodePersisterImpl
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import com.zpi.domain.common.CodeGenerator
import spock.lang.Specification
import spock.lang.Subject

class AuthCodePersisterUT extends Specification {
    def repository = Mock(AuthCodeRepository)
    def generator = Mock(CodeGenerator)

    @Subject
    private AuthCodePersister persister = new AuthCodePersisterImpl(repository, generator)

    def "should return and save authCode in repository"() {
        given:
            def value = "asdf"
            def redirectUri = "aaa"
            def scope = "bbb"
            def username = "ccc"

            def data = new TicketData(redirectUri, scope, username)
            def code = new AuthCode(value, new AuthUserData(scope, redirectUri, username))

        and:
            generator.ticketCode() >> value
            repository.save(value, code) >> null

        when:
            def result = persister.persist(data)

        then:
            1 * repository.save(value, code)

        and:
            !result.getValue().isEmpty()
    }
}
