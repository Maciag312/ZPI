package com.zpi.utils

import com.zpi.domain.common.CodeGenerator
import com.zpi.domain.common.CodeGeneratorImpl
import spock.lang.Specification
import spock.lang.Subject

class CodeGeneratorUT extends Specification {
    @Subject
    private CodeGenerator generator = new CodeGeneratorImpl()

    def "should generate ticket"() {
        when:
            def result = generator.ticketCode()

        then:
            !result.isEmpty()
            result.length() > 10

        and:
            result != generator.ticketCode()
    }

    def "should generate 2fa code"() {
        when:
            def result = generator.twoFactorCode()

        then:
            !result.isEmpty()
            result.length() == 6
            result.isInteger()

        and:
            result != generator.twoFactorCode()
    }
}
