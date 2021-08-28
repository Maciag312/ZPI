package com.zpi.token

import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuer
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuerImpl
import spock.lang.Specification
import spock.lang.Subject

class TokenIssuerUT extends Specification {
    @Subject
    private TokenIssuer issuer = new TokenIssuerImpl()

    def "should return token when data correct"() {

    }

    def "should throw exception on incorrect data"() {

    }
}
