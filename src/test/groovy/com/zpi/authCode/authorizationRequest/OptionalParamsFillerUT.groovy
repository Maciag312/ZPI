package com.zpi.authCode.authorizationRequest

import com.zpi.testUtils.CommonFixtures
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest
import com.zpi.domain.authCode.authenticationRequest.OptionalParamsFiller
import com.zpi.domain.rest.ams.AmsService
import com.zpi.domain.rest.ams.Client
import spock.lang.Specification
import spock.lang.Subject

class OptionalParamsFillerUT extends Specification {
    def ams = Mock(AmsService)

    @Subject
    private OptionalParamsFiller filler = new OptionalParamsFiller(ams)

    def "should fill missing redirect_uri"() {
        given:
            def request = AuthenticationRequest.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(null)
                    .responseType(CommonFixtures.responseType)
                    .scope(CommonFixtures.scopeList)
                    .state(CommonFixtures.state)
                    .build()

        and:
            def defaultRedirectUri = "asdfasdfsadfasdfasdf"
            def client = new Client(List.of(defaultRedirectUri), "1")
            ams.clientDetails(request.getClientId()) >> Optional.of(client)

        when:
            def filled = filler.fill(request)

        then:
            def expected = AuthenticationRequest.builder()
                    .clientId(request.getClientId())
                    .redirectUri(defaultRedirectUri)
                    .responseType(request.getResponseType())
                    .scope(request.getScope())
                    .state(request.getState())
                    .build()

            filled == expected
    }

    def "should fill missing scope"() {
        given:
            def request = AuthenticationRequest.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType(CommonFixtures.responseType)
                    .scope(null)
                    .state(CommonFixtures.state)
                    .build()

        and:
            def client = new Client(new ArrayList<String>(), "1")

            ams.clientDetails(request.getClientId()) >> Optional.of(client)

        when:
            def filled = filler.fill(request)

        then:
            def expected = AuthenticationRequest.builder()
                    .clientId(request.getClientId())
                    .redirectUri(request.getRedirectUri())
                    .responseType(request.getResponseType())
                    .scope(CommonFixtures.hardcodedScopeList)
                    .state(request.getState())
                    .build()

            filled == expected
    }
}
