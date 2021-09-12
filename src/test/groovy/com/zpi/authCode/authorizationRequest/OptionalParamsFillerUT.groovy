package com.zpi.authCode.authorizationRequest

import com.zpi.CommonFixtures
import com.zpi.domain.authCode.authenticationRequest.OptionalParamsFiller
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest
import com.zpi.domain.organization.client.Client
import com.zpi.domain.organization.client.ClientRepository
import spock.lang.Specification
import spock.lang.Subject

class OptionalParamsFillerUT extends Specification {
    def clientRepository = Mock(ClientRepository)

    @Subject
    private OptionalParamsFiller filler = new OptionalParamsFiller(clientRepository)

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
            def client = new Client("1")
            client.getAvailableRedirectUri().addAll(List.of(defaultRedirectUri));
            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)

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
            def client = new Client('1')

            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)

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
