package com.zpi.token.refreshRequest

import com.github.tomakehurst.wiremock.WireMockServer
import com.zpi.api.token.RefreshRequestDTO
import com.zpi.domain.token.TokenRepository
import com.zpi.domain.token.issuer.TokenData
import com.zpi.infrastructure.rest.ams.AmsClient
import com.zpi.testUtils.CommonFixtures
import com.zpi.testUtils.MvcRequestHelpers
import com.zpi.testUtils.ResultHelpers
import com.zpi.testUtils.wiremock.ClientMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class TokenRefreshFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private AmsClient amsClient

    @Autowired
    private WireMockServer mockServer

    @Autowired
    private MvcRequestHelpers mvcRequestHelpers

    @Autowired
    private TokenRepository tokenRepository

    private static final String baseURl = "/api/token/refresh"

    def setup() {
        ClientMocks.setupMockClientDetailsResponse(mockServer)
    }

    def cleanup() {
        tokenRepository.clear()
    }

    def "should return token when refresh token correct"() {
        given:
            def refreshToken = "asdf"
            def request = new RefreshRequestDTO(CommonFixtures.clientId, CommonFixtures.grantType, refreshToken, "profile")

        and:
            tokenRepository.save(refreshToken, new TokenData("", "", ""))

        when:
            def response = mvcRequestHelpers.postRequest(request, baseURl)

        then:
            !ResultHelpers.attributeFromResult("access_token", response).isEmpty()
            !ResultHelpers.attributeFromResult("token_type", response).isEmpty()
            !ResultHelpers.attributeFromResult("refresh_token", response).isEmpty()
            !ResultHelpers.attributeFromResult("expires_in", response).isEmpty()
    }

    def "should return error when refresh token was already used"() {
        given:
            def refreshToken = "asdf"
            def request = new RefreshRequestDTO(CommonFixtures.clientId, CommonFixtures.grantType, refreshToken, CommonFixtures.scope)

        and:
            tokenRepository.save(refreshToken, new TokenData("", "", ""))

        when:
            mvcRequestHelpers.postRequest(request, baseURl)
            def response = mvcRequestHelpers.postRequest(request, baseURl)

        then:
            !ResultHelpers.attributeFromResult("error", response).isEmpty()
            !ResultHelpers.attributeFromResult("error_description", response).isEmpty()
    }

    def "should return error when refresh token not recognized"() {
        given:
            def refreshToken = "asdf"
            def request = new RefreshRequestDTO(CommonFixtures.clientId, CommonFixtures.grantType, refreshToken, CommonFixtures.scope)

        when:
            def response = mvcRequestHelpers.postRequest(request, baseURl)

        then:
            !ResultHelpers.attributeFromResult("error", response).isEmpty()
            !ResultHelpers.attributeFromResult("error_description", response).isEmpty()
    }
}
