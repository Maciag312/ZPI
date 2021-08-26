package com.zpi.authCode.authorizationRequest

import com.zpi.CommonFixtures
import com.zpi.MvcRequestHelpers
import com.zpi.ResultHelpers
import com.zpi.UriParamsResult
import com.zpi.api.authCode.ticketRequest.RequestDTO
import com.zpi.domain.authCode.consentRequest.TicketRepository
import com.zpi.domain.client.ClientRepository
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
    private MvcRequestHelpers commonHelpers

    private static final String baseUrl = "/api/authorize"

    def setup() {
        ticketRepository.clear()
        clientRepository.clear()

        def client = CommonFixtures.client()
        clientRepository.save(client.getId(), client)
    }

    def "should redirect with success with all correct parameters"() {
        given:
            def request = CommonFixtures.requestDTO()

        when:
            def response = commonHelpers.getRequest(ResultHelpers.authParametersToUrl(request, baseUrl))

        then:
            def expected = request
            def actual = new UriParamsResult(response)
            actual.getRequest() == expected
    }

    def "should redirect with success with only required parameters"() {
        given:
            def request = CommonFixtures.requestOnlyRequiredDTO()

        when:
            def response = commonHelpers.getRequest(ResultHelpers.authParametersToUrl(request, baseUrl))

        then:
            def redirectUri = clientRepository.getByKey(request.getClientId()).get().getAvailableRedirectUri().stream().findFirst().orElse(null)

        and:
            def expected = RequestDTO.builder()
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
            def request = RequestDTO.builder().build()

        when:
            def response = commonHelpers.getRequest(ResultHelpers.authParametersToUrl(request, baseUrl))

        then:
            def actual = new UriParamsResult(response)

            !actual.getParam("error").isEmpty()
            !actual.getParam("error_description").isEmpty()
            !actual.getParam("state").isEmpty()
    }
}
