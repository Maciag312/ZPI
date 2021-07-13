package com.zpi.utils;

import spock.lang.Specification;

class IdGeneratorUT extends Specification {
    def "should return non empty id"() {
        when:
            def id = IdGenerator.generateId()

        then:
            id.toString().length() != 0
    }

    def "should return different ids for different invocations"() {
        when:
            def idA = IdGenerator.generateId()
            def idB = IdGenerator.generateId()

        then:
            idA != idB
    }
}
