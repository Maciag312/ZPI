package com.zpi.e2e

import com.github.tomakehurst.wiremock.WireMockServer
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO
import com.zpi.api.token.RefreshRequestDTO
import com.zpi.api.token.TokenRequestDTO
import com.zpi.domain.authCode.consentRequest.TicketRepository
import com.zpi.domain.organization.OrganizationRepository
import com.zpi.domain.rest.ams.Client
import com.zpi.domain.user.UserRepository
import com.zpi.infrastructure.rest.ams.AmsClient
import com.zpi.testUtils.wiremock.ClientMocks
import com.zpi.testUtils.CommonFixtures
import com.zpi.testUtils.CommonHelpers

import com.zpi.token.TokenCommonFixtures
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class AuthFlowE2E extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private AmsClient amsClient

    @Autowired
    private WireMockServer mockServer

    @Autowired
    private UserRepository userRepository

    @Autowired
    private TicketRepository ticketRepository

    @Autowired
    private OrganizationRepository organizationRepository

    @Autowired
    private CommonHelpers commonHelpers

    private static final String organizationURI = '/api/organization/';

    private static final String clientRegisterUrl(String organizationName) {
        return organizationURI + organizationName + '/client/register';
    }

    private static final String userRegisterUrl(String organizationName) {
        return organizationURI + organizationName + '/user/register'
    }

    private static final String organizationRegisterUrl = "/api/organization/register/"
    private static final String authorizeRequestUrl = "/api/authorize"
    private static final String authenticateRequestUrl = "/api/authenticate"
    private static final String consentRequestUrl = "/api/consent"
    private static final String tokenRequestUrl = "/api/token"
    private static final String refreshTokenRequestUrl = "/api/token/refresh"

    def setup() {
        userRepository.clear()
        ticketRepository.clear()
        organizationRepository.clear()

        ClientMocks.setupMockClientDetailsResponse(mockServer)
    }

    def "should perform whole oauth2 flow"() {
        given:
            def organizationName = "pizza-house-231"
            def redirectUri = CommonFixtures.redirectUri
            def client = new Client(List.of(redirectUri), CommonFixtures.clientId)
            def user = CommonFixtures.userDTO()
            def grantType = "authorization_code"
            def scope = "profile"

        when:
            commonHelpers.postRequest(organizationRegisterUrl + organizationName)
            commonHelpers.postRequest(client, clientRegisterUrl(organizationName))
            commonHelpers.postRequest(user, userRegisterUrl(organizationName))

        and:
            def request = TicketRequestDTO.builder()
                    .scope(scope)
                    .redirectUri(redirectUri)
                    .clientId(client.getId())
                    .responseType("code")
                    .state("asdffdsaasdf")
                    .build()

            def authorizeResponse = commonHelpers.getRequest(CommonHelpers.authParametersToUrl(request, authorizeRequestUrl))

        then:
            authorizeResponse.andExpect(status().isFound())
            authorizeResponse.andExpect(header().exists("Location"))

        when:
            def authenticateResponse = commonHelpers.postRequest(user, CommonHelpers.authParametersToUrl(request, authenticateRequestUrl))

        then:
            def ticket = CommonHelpers.attributeFromResult("ticket", authenticateResponse)

        when:
            def consentRequest = CommonFixtures.consentRequestDTO(ticket)
            def consentResponse = commonHelpers.postRequest(consentRequest, CommonHelpers.authParametersToUrl(request, consentRequestUrl))

        then:
            def code = CommonHelpers.attributeFromRedirectedUrlInBody("code", consentResponse)

        when:
            def tokenRequest = new TokenRequestDTO(grantType, code, client.getId(), scope)
            def tokenResponse = commonHelpers.postRequest(tokenRequest, tokenRequestUrl)

        then:
            def token = TokenCommonFixtures.parseToken(CommonHelpers.attributeFromResult("access_token", tokenResponse))
            def refreshToken = CommonHelpers.attributeFromResult("refresh_token", tokenResponse)

        when:
            def refreshRequest = new RefreshRequestDTO(client.getId(), grantType, refreshToken, scope)
            def refreshResponse = commonHelpers.postRequest(refreshRequest, refreshTokenRequestUrl)

        then:
            def refreshedToken = TokenCommonFixtures.parseToken(CommonHelpers.attributeFromResult("access_token", refreshResponse))

        and:
            refreshedToken != token
            refreshedToken.getBody().get("username_hash") == user.toHashedDomain().getLogin()
            refreshedToken.getBody().getIssuer() == ""
    }
}
