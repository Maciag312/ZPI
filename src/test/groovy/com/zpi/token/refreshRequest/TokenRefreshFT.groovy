package com.zpi.token.refreshRequest

import com.zpi.CommonFixtures
import com.zpi.MvcRequestHelpers
import com.zpi.ResultHelpers
import com.zpi.api.token.RefreshRequestDTO
import com.zpi.domain.organization.client.Client
import com.zpi.domain.organization.client.ClientRepository
import com.zpi.domain.token.issuer.TokenData
import com.zpi.domain.token.TokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
class TokenRefreshFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private MvcRequestHelpers mvcRequestHelpers

    @Autowired
    private TokenRepository tokenRepository

    @Autowired
    private ClientRepository clientRepository

    private static final String baseURl = "/api/token/refresh"

    def cleanup() {
        tokenRepository.clear()
        clientRepository.clear()
    }

    def "should return token when refresh token correct"() {
        given:
            def refreshToken = "asdf"
            def request = new RefreshRequestDTO(CommonFixtures.clientId, CommonFixtures.grantType, refreshToken, "profile")
            def client = new Client(request.getClient_id())

        and:
            tokenRepository.save(refreshToken, new TokenData("", "", ""))
            clientRepository.save(client.getId(), client)

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
