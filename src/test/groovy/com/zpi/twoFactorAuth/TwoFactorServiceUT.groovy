package com.zpi.twoFactorAuth

import com.zpi.api.common.exception.ErrorResponseException
import com.zpi.domain.twoFactorAuth.TwoFactorAuthService
import com.zpi.domain.twoFactorAuth.TwoFactorAuthServiceImpl
import com.zpi.domain.twoFactorAuth.TwoFactorData
import com.zpi.domain.twoFactorAuth.TwoFactorRepository
import spock.lang.Specification
import spock.lang.Subject

class TwoFactorServiceUT extends Specification {
    def repository = Mock(TwoFactorRepository)

    @Subject
    private TwoFactorAuthService service = new TwoFactorAuthServiceImpl(repository)

    def "should return ticket when 2fa data is correct"() {
        given:
            def data = new TwoFactorData("a", "b")

        and:
            repository.findByKey(data.getTicket()) >> Optional.of(data)

        when:
            def result = service.validate(data)

        then:
            !result.getTicket().isEmpty()
    }

    def "should return error when twoFactorTicket is incorrect"() {
        given:
            def data = new TwoFactorData("a", "b")

        and:
            repository.findByKey(data.getTicket()) >> Optional.empty()

        when:
            service.validate(data)

        then:
            def ex = thrown(ErrorResponseException)
            ex.getErrorResponse().getError().toString() == "INCORRECT_2FA_CODE"
            ex.getErrorResponse().getError_description() == "Incorrect 2FA code"
    }

    def "should return error when 2fa code is incorrect"() {
        given:
            def data = new TwoFactorData("a", "b")

        and:
            repository.findByKey(data.getTicket()) >> Optional.of(new TwoFactorData(data.getTicket(), "asdf"))

        when:
            service.validate(data)

        then:
            def ex = thrown(ErrorResponseException)
            ex.getErrorResponse().getError().toString() == "INCORRECT_2FA_CODE"
            ex.getErrorResponse().getError_description() == "Incorrect 2FA code"
    }
}
