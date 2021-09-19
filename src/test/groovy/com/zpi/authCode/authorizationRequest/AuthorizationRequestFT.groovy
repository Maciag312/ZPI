package com.zpi.authCode.authorizationRequest

import com.zpi.CommonFixtures
import com.zpi.MvcRequestHelpers
import com.zpi.ResultHelpers
import com.zpi.UriParamsResult
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO
import com.zpi.domain.authCode.consentRequest.TicketRepository
import com.zpi.domain.organization.client.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationRequestFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ClientRepository clientRepository

    @Autowired
    private TicketRepository ticketRepository

    @Autowired
    private MvcRequestHelpers mvcRequestHelpers

    private static final String baseUrl = "/api/authorize"

    def setup() {
        def client = CommonFixtures.client()
        clientRepository.save(client.getId(), client)
    }

    def cleanup() {
        ticketRepository.clear()
        clientRepository.clear()
    }

    def "should redirect with success with all correct parameters"() {
        given:
            def request = CommonFixtures.requestDTO()

        when:
            def response = mvcRequestHelpers.getRequest(ResultHelpers.authParametersToUrl(request, baseUrl))

        then:
            def expected = request
            def actual = new UriParamsResult(response)
            actual.getRequest() == expected
    }

    def "should redirect with success with only required parameters"() {
        given:
            def request = CommonFixtures.requestOnlyRequiredDTO()

        when:
            def response = mvcRequestHelpers.getRequest(ResultHelpers.authParametersToUrl(request, baseUrl))

        then:
            def redirectUri = clientRepository.findByKey(request.getClientId()).get().getAvailableRedirectUri().stream().findFirst().orElse(null)

        and:
            def expected = TicketRequestDTO.builder()
                    .clientId(request.getClientId())
                    .redirectUri(redirectUri)
                    .responseType(request.getResponseType())
                    .scope(CommonFixtures.hardcodedScope)
                    .state(request.getState())
                    .build()

            def actual = new UriParamsResult(response)
            actual.getRequest() == expected
    }

    def "should redirect with error when incorrect parameters"() {
        given:
            def request = TicketRequestDTO.builder()
                    .clientId('123')
                    .responseType('oops-wrong-code')
                    .scope(CommonFixtures.hardcodedScope)
                    .build()

        when:
            def response = mvcRequestHelpers.getRequest(ResultHelpers.authParametersToUrl(request, baseUrl))

        then:
            def actual = new UriParamsResult(response)

            !actual.getParam("error").isEmpty()
            !actual.getParam("error_description").isEmpty()
            !actual.getParam("state").isEmpty()
    }
}
