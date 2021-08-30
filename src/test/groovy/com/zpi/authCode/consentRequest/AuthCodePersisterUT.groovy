package com.zpi.authCode.consentRequest

import com.zpi.domain.authCode.consentRequest.AuthCode
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodePersister
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodePersisterImpl
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import spock.lang.Specification
import spock.lang.Subject

class AuthCodePersisterUT extends Specification {
    def repository = Mock(AuthCodeRepository)

    @Subject
    private AuthCodePersister persister = new AuthCodePersisterImpl(repository)

    def "should return and save authCode in repository"() {
        given:
            repository.save(_ as String, _ as AuthCode) >> null

        when:
            def result = persister.persist()

        then:
            1 * repository.save(_ as String, _ as AuthCode)

        and:
            !result.getValue().isEmpty()
    }
}
