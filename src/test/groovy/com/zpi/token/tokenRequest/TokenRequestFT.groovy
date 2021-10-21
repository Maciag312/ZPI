package com.zpi.token.tokenRequest

import com.github.tomakehurst.wiremock.WireMockServer
import com.zpi.api.token.TokenRequestDTO
import com.zpi.domain.authCode.consentRequest.AuthCode
import com.zpi.domain.authCode.consentRequest.AuthUserData
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import com.zpi.domain.token.issuer.config.TokenIssuerConfigProvider
import com.zpi.infrastructure.rest.ams.AmsClient
import com.zpi.testUtils.CommonFixtures
import com.zpi.testUtils.MvcRequestHelpers
import com.zpi.testUtils.ResultHelpers
import com.zpi.testUtils.wiremock.ClientMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@SpringBootTest(properties = "security.jwt.token.secret-key=AJASFDNUS812DAMNXMANSDHQHW83183JD18JJ1HFG8JXJ12JSH1XCHBUJ28X2JH12J182XJH1F3H1JS81G7RESHD13H71")
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
class TokenRequestFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private AmsClient ams

    @Autowired
    private WireMockServer mockServer

    @Autowired
    private MvcRequestHelpers mvcRequestHelpers

    @Autowired
    private AuthCodeRepository authCodeRepository

    @Autowired
    private TokenIssuerConfigProvider configProvider

    private static final String baseUrl = "/api/token"

    def setup() {
        ClientMocks.setupMockClientDetailsResponse(mockServer)
    }

    def cleanup() {
        authCodeRepository.clear()
    }

    def "should return correct response when provided data is correct"() {
        given:
            def authCode = UUID.randomUUID().toString()
            def client = CommonFixtures.client()

            def request = new TokenRequestDTO(CommonFixtures.grantType, authCode, client.getId(), "profile")

        and:
            authCodeRepository.save(authCode, new AuthCode(authCode, new AuthUserData("", "", "")))

        when:
            def response = mvcRequestHelpers.postRequest(request, baseUrl)

        then:
            !ResultHelpers.attributeFromResult("access_token", response).isEmpty()
            ResultHelpers.attributeFromResult("token_type", response) == "Bearer"
            !ResultHelpers.attributeFromResult("refresh_token", response).isEmpty()
            ResultHelpers.attributeFromResult("expires_in", response) == "3600"

        and:
            ResultHelpers.headerFromResult("Cache-Control", response).contains("no-store")
            ResultHelpers.headerFromResult("Pragma", response).contains("no-cache")
    }

    def "should return error on authCode used twice"() {
        given:
            def authCode = UUID.randomUUID().toString()
            def client = CommonFixtures.client()

            def request = new TokenRequestDTO(CommonFixtures.grantType, authCode, client.getId(), "profile")

        and:
            authCodeRepository.save(authCode, new AuthCode(authCode, new AuthUserData("", "", "")))

        when:
            mvcRequestHelpers.postRequest(request, baseUrl)
            def response = mvcRequestHelpers.postRequest(request, baseUrl)

        then:
            !ResultHelpers.attributeFromResult("error", response).isEmpty()
            !ResultHelpers.attributeFromResult("error_description", response).isEmpty()
    }

    def "should return error on incorrect data"() {
        given:
            def authCode = "codeasdffdsa"

            def client = CommonFixtures.client()
            def request = new TokenRequestDTO(null, authCode, client.getId(), "profile")

        when:
            def response = mvcRequestHelpers.postRequest(request, baseUrl)

        then:
            !ResultHelpers.attributeFromResult("error", response).isEmpty()
            !ResultHelpers.attributeFromResult("error_description", response).isEmpty()
    }
}
